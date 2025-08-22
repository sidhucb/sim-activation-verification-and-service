package com.notification.controller;

import com.notification.entity.Notification;
import com.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @PostMapping("/send")
    public String sendNotification(@RequestBody Notification notification) {
        service.sendNotification(notification);
        return "Notification sent successfully!";
    }
}
