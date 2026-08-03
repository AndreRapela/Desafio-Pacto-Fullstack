package com.pacto.recrutamento.dto.job;

import com.pacto.recrutamento.domain.Job;
import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.JobStatus;

import java.time.LocalDateTime;

public record JobResponse(
        long id,
        String title,
        String description,
        String requirements,
        int minCompanyTime,
        JobStatus status,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean eligible
) {
    public JobResponse(Job job, UserAccount user) {
        this(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getRequirements(),
                job.getMinCompanyTime(),
                job.getStatus(),
                job.getCreatedBy().getName(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                user.monthsAtCompany() >= job.getMinCompanyTime()
        );
    }
}
