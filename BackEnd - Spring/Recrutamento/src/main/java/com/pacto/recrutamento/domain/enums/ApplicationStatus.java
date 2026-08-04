package com.pacto.recrutamento.domain.enums;

public enum ApplicationStatus {

    SUBMITTED("enviada"),
    UNDER_REVIEW("em análise"),
    APPROVED("aprovada"),
    REJECTED("não aprovada");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
