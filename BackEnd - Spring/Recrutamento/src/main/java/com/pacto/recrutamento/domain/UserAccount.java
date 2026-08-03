package com.pacto.recrutamento.domain;

import com.pacto.recrutamento.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tb_user")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long  id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    public int monthsAtCompany() {
        LocalDate start = hireDate == null ? LocalDate.now() : hireDate;
        Period period = Period.between(start, LocalDate.now());
        return Math.max(0, period.getYears() * 12 + period.getMonths());
    }

}

