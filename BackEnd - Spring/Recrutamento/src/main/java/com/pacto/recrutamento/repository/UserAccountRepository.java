package com.pacto.recrutamento.repository;

import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enuns.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase (String email);

}
