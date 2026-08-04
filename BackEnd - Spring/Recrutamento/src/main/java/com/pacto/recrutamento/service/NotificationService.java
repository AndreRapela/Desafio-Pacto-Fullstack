package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.Notification;
import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.dto.notification.NotificationResponse;
import com.pacto.recrutamento.exception.NotFoundException;
import com.pacto.recrutamento.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final CurrentUserService currentUserService;


    @Transactional
    public void create(UserAccount recipient, String title, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        repository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list(Authentication authentication) {
        UserAccount user = currentUserService.require(authentication);
        return repository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream().map(NotificationResponse::new).toList();
    }


    @Transactional
    public NotificationResponse markRead(Long id, Authentication authentication) {
        UserAccount user = currentUserService.require(authentication);
        Notification notification = repository.findById(id)
                .filter(item -> Objects.equals(item.getRecipient().getId(),user.getId()))
                .orElseThrow(() -> new NotFoundException("Notificação não encontrada."));
        notification.markAsRead();
        return new NotificationResponse(notification);
    }

    @Transactional(readOnly = true)
    public NotificationResponse find(Long id, Authentication authentication) {
        UserAccount user = currentUserService.require(authentication);

        Notification notification = repository.findById(id)
                .filter(item -> Objects.equals(item.getRecipient().getId(),user.getId()))
                .orElseThrow(() -> new NotFoundException("Notificação não encontrada."));
        return new NotificationResponse(notification);
    }


}
