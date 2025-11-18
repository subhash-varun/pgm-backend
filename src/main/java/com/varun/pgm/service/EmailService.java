package com.varun.pgm.service;

import com.varun.pgm.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send notification email
     */
    public void sendNotificationEmail(Notification notification) {
        try {
            // TODO: Get user email from user service
            // For now, we'll use a placeholder email
            String recipientEmail = getUserEmail(notification.getTargetUserId());

            if (recipientEmail == null || recipientEmail.isEmpty()) {
                logger.warn("No email found for user {}", notification.getTargetUserId());
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("PGM Notification: " + notification.getTitle());
            message.setText(buildEmailContent(notification));

            mailSender.send(message);
            logger.info("Email notification sent to {} for notification {}", recipientEmail, notification.getId());

        } catch (Exception e) {
            logger.error("Error sending email notification", e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }

    /**
     * Build email content from notification
     */
    private String buildEmailContent(Notification notification) {
        StringBuilder content = new StringBuilder();
        content.append("Dear User,\n\n");
        content.append("You have received a new notification:\n\n");
        content.append("Title: ").append(notification.getTitle()).append("\n");

        if (notification.getBody() != null && !notification.getBody().isEmpty()) {
            content.append("Message: ").append(notification.getBody()).append("\n");
        }

        content.append("Type: ").append(notification.getType()).append("\n");
        content.append("Date: ").append(notification.getCreatedAt()).append("\n\n");

        content.append("Please log in to your account to view more details.\n\n");
        content.append("Best regards,\n");
        content.append("PG Management System");

        return content.toString();
    }

    /**
     * Get user email by user ID
     * TODO: Implement proper user service integration
     */
    private String getUserEmail(Long userId) {
        // Placeholder implementation - should be replaced with actual user service call
        // For now, return a test email based on user ID
        return "user" + userId + "@example.com";
    }
}
