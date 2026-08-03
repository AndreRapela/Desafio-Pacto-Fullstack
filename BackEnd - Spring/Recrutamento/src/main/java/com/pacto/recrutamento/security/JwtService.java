package com.pacto.recrutamento.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.pacto.recrutamento.domain.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service

public class JwtService {
    private final Algorithm algoritmh;
    private final long expirationTime;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-time}") long expirationTime) {
        this.algoritmh = Algorithm.HMAC256(secret);
        this.expirationTime = expirationTime;
    }

    public String generate(UserAccount userAccount){
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer("Recrutamento")
                .withSubject(userAccount.getEmail())
                .withClaim("userId", userAccount.getId())
                .withClaim("role", userAccount.getRole().name())
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(expirationTime))
                .sign(algoritmh);
    }

    public String getSubject(String token){
        try {
            return JWT.require(algoritmh)
                    .withIssuer("Recrutamento")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
