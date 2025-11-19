package com.varun.pgm.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varun.pgm.dto.request.NotificationSendRequest;
import com.varun.pgm.dto.request.NotificationPreferencesRequest;
import com.varun.pgm.dto.response.NotificationPageResponse;
import com.varun.pgm.dto.response.NotificationResponse;
import com.varun.pgm.dto.response.NotificationPreferencesResponse;
import com.varun.pgm.entity.Notification;
import com.varun.pgm.entity.NotificationPreferences;
import com.varun.pgm.entity.Role;
import com.varun.pgm.entity.UserRole;
import com.varun.pgm.repository.NotificationPreferencesRepository;
import com.varun.pgm.repository.NotificationRepository;
import com.varun.pgm.repository.RoleRepository;
import com.varun.pgm.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final String UNREAD_COUNT_KEY_PREFIX = "notification:unread:";

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferencesRepository preferencesRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailService emailService;

    /**
     * Send a notification to specified targets
     */
    @Transactional
    public NotificationResponse sendNotification(NotificationSendRequest request, Long senderId) {
        logger.info("Sending notification: targetRole={}, targetUserId={}, targetPropertyId={}",
            request.getTargetRole(), request.getTargetUserId(), request.getTargetPropertyId());

        // If targeting a specific role, create notifications for all users in that role
        if (request.getTargetRole() != null && !request.getTargetRole().trim().isEmpty()) {
            return sendRoleBasedNotification(request, senderId);
        }

        // If targeting a specific property, create notifications for all users associated with that property
        if (request.getTargetPropertyId() != null) {
            return sendPropertyBasedNotification(request, senderId);
        }

        // Single user notification
        return sendSingleUserNotification(request, senderId);
    }

    /**
     * Send notification to a specific user
     */
    private NotificationResponse sendSingleUserNotification(NotificationSendRequest request, Long senderId) {
        Notification notification = createNotification(request, senderId, request.getTargetUserId(), null, null);

        Notification savedNotification = notificationRepository.save(notification);

        // Send real-time notification
        sendRealTimeNotification(savedNotification);

        // Send fallback notifications if needed
        sendFallbackNotifications(savedNotification, request.getPriority());

        // Update unread count cache
        updateUnreadCount(savedNotification.getTargetUserId());

        return convertToResponse(savedNotification);
    }

    /**
     * Send notification to all users in a specific role
     */
    private NotificationResponse sendRoleBasedNotification(NotificationSendRequest request, Long senderId) {
        // Find the role by name
        Optional<Role> roleOpt = roleRepository.findByName(request.getTargetRole());
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("Role not found: " + request.getTargetRole());
        }

        Role role = roleOpt.get();

        // Get all users in this role
        List<UserRole> userRoles = userRoleRepository.findById_RoleId(role.getId());

        // Create notifications for each user in the role
        List<Notification> notifications = userRoles.stream()
            .map(userRole -> {
                Long targetUserId = getUserIdFromUserRole(userRole);
                return createNotification(request, senderId, targetUserId, null, null);
            })
            .collect(Collectors.toList());

        if (notifications.isEmpty()) {
            throw new RuntimeException("No users found in role: " + request.getTargetRole());
        }

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

        // Send real-time notifications to role topic and individual users
        NotificationResponse roleNotificationResponse = new NotificationResponse(
            savedNotifications.get(0).getId(), // id of first notification
            request.getTitle(),
            request.getBody(),
            request.getType(),
            senderId,
            null, // targetUserId
            request.getTargetRole(),
            null, // targetPropertyId
            request.getPayload(),
            false, // isRead
            LocalDateTime.now(),
            true, // delivered
            0 // deliveryAttempts
        );

        // Send to role topic
        messagingTemplate.convertAndSend("/topic/role/" + request.getTargetRole(), roleNotificationResponse);
        logger.info("Sent notification to topic: /topic/role/{}", request.getTargetRole());

        for (Notification notification : savedNotifications) {
            sendRealTimeNotification(notification);
            sendFallbackNotifications(notification, request.getPriority());
            updateUnreadCount(notification.getTargetUserId());
        }

        // Return the role notification response (shows targetRole)
        return roleNotificationResponse;
    }

    /**
     * Send notification to all users associated with a property
     */
    private NotificationResponse sendPropertyBasedNotification(NotificationSendRequest request, Long senderId) {
        // TODO: Implement property-based notification logic
        // For now, create a single notification with property targeting
        Notification notification = createNotification(request, senderId, null, null, request.getTargetPropertyId());

        Notification savedNotification = notificationRepository.save(notification);

        // Send real-time notification
        sendRealTimeNotification(savedNotification);

        return convertToResponse(savedNotification);
    }

    /**
     * Create a notification entity
     */
    private Notification createNotification(NotificationSendRequest request, Long senderId,
                                          Long targetUserId, String targetRole, Long targetPropertyId) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setBody(request.getBody());
        notification.setType(request.getType());
        notification.setSenderId(senderId);
        notification.setTargetUserId(targetUserId);
        notification.setTargetRole(targetRole);
        notification.setTargetPropertyId(targetPropertyId);
        notification.setIsRead(false);
        notification.setDelivered(false);
        notification.setDeliveryAttempts(0);

        // Convert payload to JSON string
        if (request.getPayload() != null && !request.getPayload().isEmpty()) {
            try {
                notification.setPayload(objectMapper.writeValueAsString(request.getPayload()));
            } catch (Exception e) {
                logger.error("Error serializing notification payload", e);
            }
        }

        return notification;
    }

    /**
     * Extract user ID from UserRole entity
     */
    private Long getUserIdFromUserRole(UserRole userRole) {
        return userRole.getId().getUserId();
    }

    /**
     * Get paginated notifications for a user
     */
    public NotificationPageResponse getUserNotifications(Long userId, int page, int size, Boolean read) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Notification> notificationPage;
        if (read != null) {
            if (read) {
                notificationPage = notificationRepository.findByUserIdIncludingRoles(userId, pageable);
            } else {
                // For unread only, we need to filter
                List<Notification> allNotifications = notificationRepository.findByUserIdIncludingRoles(userId, Pageable.unpaged()).getContent();
                List<Notification> unreadNotifications = allNotifications.stream()
                    .filter(n -> !n.getIsRead())
                    .skip(page * size)
                    .limit(size)
                    .collect(Collectors.toList());

                // Create a custom page for unread notifications
                notificationPage = new Page<Notification>() {
                    @Override
                    public int getTotalPages() {
                        long totalUnread = allNotifications.stream().filter(n -> !n.getIsRead()).count();
                        return (int) Math.ceil((double) totalUnread / size);
                    }

                    @Override
                    public long getTotalElements() {
                        return allNotifications.stream().filter(n -> !n.getIsRead()).count();
                    }

                    @Override
                    public <U> Page<U> map(java.util.function.Function<? super Notification, ? extends U> converter) {
                        return null;
                    }

                    @Override
                    public int getNumber() {
                        return page;
                    }

                    @Override
                    public int getSize() {
                        return size;
                    }

                    @Override
                    public int getNumberOfElements() {
                        return unreadNotifications.size();
                    }

                    @Override
                    public List<Notification> getContent() {
                        return unreadNotifications;
                    }

                    @Override
                    public boolean hasContent() {
                        return !unreadNotifications.isEmpty();
                    }

                    @Override
                    public Sort getSort() {
                        return Sort.by("createdAt").descending();
                    }

                    @Override
                    public boolean isFirst() {
                        return page == 0;
                    }

                    @Override
                    public boolean isLast() {
                        return page >= getTotalPages() - 1;
                    }

                    @Override
                    public boolean hasNext() {
                        return page < getTotalPages() - 1;
                    }

                    @Override
                    public boolean hasPrevious() {
                        return page > 0;
                    }

                    @Override
                    public Pageable nextPageable() {
                        return PageRequest.of(page + 1, size, getSort());
                    }

                    @Override
                    public Pageable previousPageable() {
                        return PageRequest.of(page - 1, size, getSort());
                    }

                    @Override
                    public java.util.Iterator<Notification> iterator() {
                        return unreadNotifications.iterator();
                    }
                };
            }
        } else {
            notificationPage = notificationRepository.findByUserIdIncludingRoles(userId, pageable);
        }

        List<NotificationResponse> responses = notificationPage.getContent().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());

        return new NotificationPageResponse(
            responses,
            notificationPage.getNumber(),
            notificationPage.getTotalPages(),
            notificationPage.getTotalElements(),
            notificationPage.hasNext(),
            notificationPage.hasPrevious()
        );
    }

    /**
     * Get unread count for a user
     */
    public Long getUnreadCount(Long userId) {
        // Try to get from cache first if Redis is available
        if (redisTemplate != null) {
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + userId;
            Long cachedCount = (Long) redisTemplate.opsForValue().get(cacheKey);
            if (cachedCount != null) {
                return cachedCount;
            }
        }

        // Fallback to database query
        Long count = notificationRepository.countUnreadByUserIdIncludingRoles(userId);

        // Cache the result if Redis is available
        if (redisTemplate != null) {
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(cacheKey, count);
        }

        return count;
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            // Ensure user can only mark notifications that are targeted to them
            // (either directly or through their role)
            boolean canMarkAsRead = false;

            if (notification.getTargetUserId() != null && notification.getTargetUserId().equals(userId)) {
                canMarkAsRead = true;
            } else if (notification.getTargetRole() != null) {
                // Check if user has the target role
                List<UserRole> userRoles = userRoleRepository.findById_UserIdAndUserType(userId, UserRole.UserType.ADMIN);
                userRoles.addAll(userRoleRepository.findById_UserIdAndUserType(userId, UserRole.UserType.STAFF));
                userRoles.addAll(userRoleRepository.findById_UserIdAndUserType(userId, UserRole.UserType.TENANT));

                canMarkAsRead = userRoles.stream()
                    .anyMatch(ur -> ur.getRole().getName().equals(notification.getTargetRole()));
            }

            if (canMarkAsRead) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
                updateUnreadCount(userId);
            }
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserIdIncludingRoles(userId);
        updateUnreadCount(userId);
    }

    /**
     * Get notification preferences for a user
     */
    public NotificationPreferencesResponse getUserPreferences(Long userId) {
        Optional<NotificationPreferences> preferencesOpt = preferencesRepository.findByUserId(userId);
        if (preferencesOpt.isPresent()) {
            return convertPreferencesToResponse(preferencesOpt.get());
        }

        // Return default preferences if not set
        NotificationPreferences defaultPrefs = new NotificationPreferences();
        defaultPrefs.setUserId(userId);
        defaultPrefs.setEmailEnabled(true);
        defaultPrefs.setPushEnabled(true);
        defaultPrefs.setSmsEnabled(false);
        defaultPrefs.setWebEnabled(true);
        defaultPrefs.setCreatedAt(LocalDateTime.now());

        return convertPreferencesToResponse(defaultPrefs);
    }

    /**
     * Update notification preferences for a user
     */
    @Transactional
    public NotificationPreferencesResponse updateUserPreferences(Long userId, NotificationPreferencesRequest request) {
        NotificationPreferences preferences = preferencesRepository.findByUserId(userId)
            .orElse(new NotificationPreferences());

        preferences.setUserId(userId);
        preferences.setEmailEnabled(request.getEmailEnabled());
        preferences.setPushEnabled(request.getPushEnabled());
        preferences.setSmsEnabled(request.getSmsEnabled());
        preferences.setWebEnabled(request.getWebEnabled());

        if (preferences.getId() == null) {
            preferences.setCreatedAt(LocalDateTime.now());
        }

        NotificationPreferences saved = preferencesRepository.save(preferences);
        return convertPreferencesToResponse(saved);
    }

    /**
     * Send real-time notification via WebSocket
     */
    private void sendRealTimeNotification(Notification notification) {
        try {
            NotificationResponse response = convertToResponse(notification);

            // Send to specific user
            if (notification.getTargetUserId() != null) {
                messagingTemplate.convertAndSendToUser(
                    notification.getTargetUserId().toString(),
                    "/queue/notifications",
                    response
                );
            }

            // Send to role-based topic
            if (notification.getTargetRole() != null) {
                messagingTemplate.convertAndSend(
                    "/topic/role/" + notification.getTargetRole(),
                    response
                );
            }

            // Send to property-based topic
            if (notification.getTargetPropertyId() != null) {
                messagingTemplate.convertAndSend(
                    "/topic/property/" + notification.getTargetPropertyId(),
                    response
                );
            }

            // Mark as delivered
            notification.setDelivered(true);
            notificationRepository.save(notification);

        } catch (Exception e) {
            logger.error("Error sending real-time notification", e);
            notification.setDeliveryAttempts(notification.getDeliveryAttempts() + 1);
            notificationRepository.save(notification);
        }
    }

    /**
     * Send fallback notifications (email, SMS, etc.)
     */
    private void sendFallbackNotifications(Notification notification, String priority) {
        // Get user preferences
        NotificationPreferencesResponse preferences = getUserPreferences(notification.getTargetUserId());

        // Send email for high priority or if email is enabled
        if (("HIGH".equals(priority) || "URGENT".equals(priority) || preferences.getEmailEnabled())) {
            try {
                emailService.sendNotificationEmail(notification);
            } catch (Exception e) {
                logger.error("Error sending email notification", e);
            }
        }

        // TODO: Implement SMS notifications if needed
        // if (preferences.getSmsEnabled()) {
        //     smsService.sendNotificationSms(notification);
        // }
    }

    /**
     * Update unread count in cache and notify user
     */
    private void updateUnreadCount(Long userId) {
        if (redisTemplate != null) {
            String cacheKey = UNREAD_COUNT_KEY_PREFIX + userId;
            Long count = notificationRepository.countUnreadByTargetUserId(userId);
            redisTemplate.opsForValue().set(cacheKey, count);
        }

        // Send real-time unread count update to user
        try {
            Long unreadCount = getUnreadCount(userId);
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notification-count",
                Map.of("unreadCount", unreadCount)
            );
        } catch (Exception e) {
            logger.error("Error sending unread count update", e);
        }
    }

    /**
     * Convert Notification entity to response DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        Map<String, Object> payload = null;
        if (notification.getPayload() != null && !notification.getPayload().isEmpty()) {
            try {
                payload = objectMapper.readValue(notification.getPayload(),
                    new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                logger.error("Error deserializing notification payload", e);
            }
        }

        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getBody(),
            notification.getType(),
            notification.getSenderId(),
            notification.getTargetUserId(),
            notification.getTargetRole(),
            notification.getTargetPropertyId(),
            payload,
            notification.getIsRead(),
            notification.getCreatedAt(),
            notification.getDelivered(),
            notification.getDeliveryAttempts()
        );
    }

    /**
     * Convert NotificationPreferences entity to response DTO
     */
    private NotificationPreferencesResponse convertPreferencesToResponse(NotificationPreferences preferences) {
        return new NotificationPreferencesResponse(
            preferences.getId(),
            preferences.getUserId(),
            preferences.getEmailEnabled(),
            preferences.getPushEnabled(),
            preferences.getSmsEnabled(),
            preferences.getWebEnabled(),
            preferences.getCreatedAt()
        );
    }
}
