package com.pacto.recrutamento.controller;

import com.pacto.recrutamento.dto.notification.NotificationResponse;
import com.pacto.recrutamento.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @GetMapping
    public List<NotificationResponse> list(Authentication authentication) {
        return service.list(authentication);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable Long id, Authentication authentication) {
        return service.markRead(id, authentication);
    }
}
