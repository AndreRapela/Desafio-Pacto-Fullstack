package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.Role;
import com.pacto.recrutamento.dto.auth.LoginRequest;
import com.pacto.recrutamento.dto.auth.RegisterRequest;
import com.pacto.recrutamento.exception.BusinessException;
import com.pacto.recrutamento.repository.UserAccountRepository;
import com.pacto.recrutamento.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService service;

    @Test
    void shouldRejectDuplicatedEmail() {
        RegisterRequest request = registerRequest();

        when(userRepository.existsByEmailIgnoreCase(request.email()))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> service.register(request)
        );
    }

    @Test
    void shouldRegisterCandidateWithNormalizedData() {
        LocalDate hireDate = LocalDate.now().minusYears(2);

        RegisterRequest request = new RegisterRequest(
                "  Candidato  ",
                "CANDIDATO@CANDIDATO.COM",
                "candidato123",
                hireDate
        );

        when(userRepository.existsByEmailIgnoreCase(request.email()))
                .thenReturn(false);
        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");
        when(userRepository.save(any(UserAccount.class)))
                .thenAnswer(invocation -> {
                    UserAccount user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });
        when(jwtService.generate(any(UserAccount.class)))
                .thenReturn("jwt-token");

        var response = service.register(request);

        ArgumentCaptor<UserAccount> userCaptor =
                ArgumentCaptor.forClass(UserAccount.class);

        verify(userRepository).save(userCaptor.capture());

        UserAccount saved = userCaptor.getValue();

        assertEquals("Candidato", saved.getName());
        assertEquals("candidato@candidato.com", saved.getEmail());
        assertEquals("encoded-password", saved.getPassword());
        assertEquals(Role.CANDIDATE, saved.getRole());
        assertEquals(hireDate, saved.getHireDate());
        assertEquals("jwt-token", response.token());
    }

    @Test
    void shouldAuthenticateAndReturnUserSession() {
        LoginRequest request = new LoginRequest(
                "candidato@candidato.com",
                "candidato123"
        );

        UserAccount user = candidate();

        when(userRepository.findByEmailIgnoreCase(request.email()))
                .thenReturn(Optional.of(user));
        when(jwtService.generate(user)).thenReturn("jwt-token");

        var response = service.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager).authenticate(authCaptor.capture());

        assertEquals(
                request.email(),
                authCaptor.getValue().getPrincipal()
        );
        assertEquals(
                request.password(),
                authCaptor.getValue().getCredentials()
        );
        assertEquals(user.getId(), response.userId());
        assertEquals(Role.CANDIDATE, response.role());
        assertEquals("jwt-token", response.token());
    }

    private RegisterRequest registerRequest() {
        return new RegisterRequest(
                "Candidato",
                "candidato@candidato.com",
                "candidato123",
                LocalDate.now().minusYears(1)
        );
    }

    private UserAccount candidate() {
        UserAccount user = new UserAccount();
        user.setId(1L);
        user.setName("Candidato");
        user.setEmail("candidato@candidato.com");
        user.setRole(Role.CANDIDATE);
        user.setHireDate(LocalDate.now().minusYears(2));
        user.setActive(true);
        return user;
    }
}
