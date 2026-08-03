package com.pacto.recrutamento.dto.job;

import com.pacto.recrutamento.domain.enums.JobStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank String description,
        @NotBlank String requirements,
        @Min(0) int minMonthsAtCompany,
        @NotNull JobStatus status
        ) {}
