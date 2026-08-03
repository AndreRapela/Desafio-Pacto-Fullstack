package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.Role;
import com.pacto.recrutamento.dto.auth.AuthResponse;
import com.pacto.recrutamento.dto.auth.LoginRequest;
import com.pacto.recrutamento.dto.auth.RegisterRequest;
import com.pacto.recrutamento.exception.BusinessException;
import com.pacto.recrutamento.repository.UserAccountRepository;
import com.pacto.recrutamento.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Já existe um usuário com este e-mail.");
        }
        UserAccount user = new UserAccount();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CANDIDATE);
        user.setHireDate(request.hireDate());
        user.setActive(true);
        userRepository.save(user);
        return response(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserAccount user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));
        return response(user);
    }

    private AuthResponse response(UserAccount user) {
        return new AuthResponse(jwtService.generate(user), user.getId(), user.getName(),
                user.getEmail(), user.getRole());
    }
}
