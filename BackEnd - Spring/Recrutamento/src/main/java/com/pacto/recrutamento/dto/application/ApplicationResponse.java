package com.pacto.recrutamento.dto.application;

import com.pacto.recrutamento.domain.enuns.ApplicationStatus;

import java.time.Instant;
import java.time.LocalDate;


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
    Instant createdAt,
    Instant updatedAt
){}
