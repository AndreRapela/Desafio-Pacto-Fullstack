package com.pacto.recrutamento.dto.application;

import com.pacto.recrutamento.domain.enuns.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateApplicationStatusRequest(
        @NotNull ApplicationStatus status,
        @Size(max = 4000) String feedback
) {
}
