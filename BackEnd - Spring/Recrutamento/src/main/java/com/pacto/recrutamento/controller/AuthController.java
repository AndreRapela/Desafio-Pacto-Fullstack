package com.pacto.recrutamento.controller;

import com.pacto.recrutamento.dto.auth.AuthResponse;
import com.pacto.recrutamento.dto.auth.LoginRequest;
import com.pacto.recrutamento.dto.auth.RegisterRequest;
import com.pacto.recrutamento.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }
}
