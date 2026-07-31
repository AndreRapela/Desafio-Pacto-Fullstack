package com.pacto.recrutamento.dto.job;

import com.pacto.recrutamento.domain.enuns.JobStatus;

import java.time.LocalDateTime;

public record JobResponse(
        long id,
        String title,
        String description,
        String requirements,
        int minMonthsAtCompany,
        JobStatus status,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean eligible
) {
}
