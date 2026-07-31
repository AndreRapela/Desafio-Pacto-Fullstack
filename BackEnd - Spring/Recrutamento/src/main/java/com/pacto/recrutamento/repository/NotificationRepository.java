package com.pacto.recrutamento.repository;

import com.pacto.recrutamento.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
