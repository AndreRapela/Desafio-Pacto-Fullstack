package com.pacto.recrutamento.repository;

import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase (String email);
    boolean existsByEmailIgnoreCase(String email);
    List<UserAccount> findByRoleAndActiveTrue(Role role);
}
