package com.pacto.recrutamento.dto.auth;

import com.pacto.recrutamento.domain.enuns.Role;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        Role role
) {
}
