package com.varun.pgm.repository;

import com.varun.pgm.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find notifications for a specific user (direct + role-based)
    @Query("SELECT n FROM Notification n WHERE n.targetUserId = :userId OR " +
           "(n.targetRole IS NOT NULL AND n.targetRole IN " +
           "(SELECT r.name FROM UserRole ur JOIN ur.role r WHERE ur.id.userId = :userId)) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdIncludingRoles(@Param("userId") Long userId, Pageable pageable);

    // Count unread notifications for a specific user (direct + role-based)
    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.targetUserId = :userId OR " +
           "(n.targetRole IS NOT NULL AND n.targetRole IN " +
           "(SELECT r.name FROM UserRole ur JOIN ur.role r WHERE ur.id.userId = :userId))) " +
           "AND n.isRead = false")
    Long countUnreadByUserIdIncludingRoles(@Param("userId") Long userId);

    // Find unread notifications for a specific user
    @Query("SELECT n FROM Notification n WHERE n.targetUserId = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByTargetUserId(@Param("userId") Long userId);

    // Count unread notifications for a specific user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.targetUserId = :userId AND n.isRead = false")
    Long countUnreadByTargetUserId(@Param("userId") Long userId);

    // Find notifications by role
    @Query("SELECT n FROM Notification n WHERE n.targetRole = :role ORDER BY n.createdAt DESC")
    List<Notification> findByTargetRole(@Param("role") String role);

    // Find notifications by property
    @Query("SELECT n FROM Notification n WHERE n.targetPropertyId = :propertyId ORDER BY n.createdAt DESC")
    List<Notification> findByTargetPropertyId(@Param("propertyId") Long propertyId);

    // Find notifications by type
    List<Notification> findByType(String type);

    // Find notifications created after a specific date
    List<Notification> findByCreatedAtAfter(LocalDateTime createdAt);

    // Mark all notifications as read for a user (direct + role-based)
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.isRead = false AND " +
           "(n.targetUserId = :userId OR " +
           "(n.targetRole IS NOT NULL AND n.targetRole IN " +
           "(SELECT r.name FROM UserRole ur JOIN ur.role r WHERE ur.id.userId = :userId)))")
    void markAllAsReadByUserIdIncludingRoles(@Param("userId") Long userId);

    // Find notifications that need delivery (not delivered and attempts < max)
    @Query("SELECT n FROM Notification n WHERE n.delivered = false AND n.deliveryAttempts < :maxAttempts")
    List<Notification> findUndeliveredNotifications(@Param("maxAttempts") int maxAttempts);
}
