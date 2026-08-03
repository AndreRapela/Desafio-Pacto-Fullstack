package com.pacto.recrutamento.dto.application;

import com.pacto.recrutamento.domain.Application;
import com.pacto.recrutamento.domain.enuns.ApplicationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;


public record ApplicationResponse (
    Long id,
    Long jobId,
    String jobTitle,
    Long candidateId,
    String candidateName,
    String candidateEmail,
    LocalDate companyStartDate,
    int monthsAtCompany,
    ApplicationStatus status,
    String feedback,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){
    public ApplicationResponse(Application application){
        this( application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getCandidate().getId(),
                application.getCandidate().getName(),
                application.getCandidate().getEmail(),
                application.getCandidate().getHireDate(),
                application.getCandidate().monthsAtCompany(),
                application.getStatus(),
                application.getFeedback(),
                application.getCreatedAt(),
                application.getUpdatedAt());
    }
}
