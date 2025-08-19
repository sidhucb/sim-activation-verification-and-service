package com.notification.service;

import com.notification.entity.Notification;
import com.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository repository;

    @Override
    public void sendNotification(Notification notification) {
        // Save to DB
        repository.save(notification);

        // Send Email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipientEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());

        mailSender.send(message);
    }
}
