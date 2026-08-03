package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserAccountRepository repository;

    public UserAccount require(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Usuário autenticado não encontrado.");
        }
        return repository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Usuário autenticado não encontrado."));
    }
}
