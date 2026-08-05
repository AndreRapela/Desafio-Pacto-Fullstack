package com.pacto.recrutamento.config;

import com.pacto.recrutamento.domain.Job;
import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.JobStatus;
import com.pacto.recrutamento.domain.enums.Role;
import com.pacto.recrutamento.repository.JobRepository;
import com.pacto.recrutamento.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(UserAccountRepository userRepository,
                           JobRepository jobRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            UserAccount admin = userRepository.findByEmailIgnoreCase("admin@admin.com")
                    .orElseGet(() -> userRepository.save(createUser(
                            "Admin",
                            "admin@admin.com",
                            "admin123",
                            Role.ADMIN,
                            LocalDate.now().minusYears(4),
                            passwordEncoder
                    )));

            userRepository.findByEmailIgnoreCase("teste@teste.com")
                    .orElseGet(() -> userRepository.save(createUser(
                            "candidato",
                            "candidato@candidato.com",
                            "candidato123",
                            Role.CANDIDATE,
                            LocalDate.now().minusYears(2),
                            passwordEncoder
                    )));

            if (jobRepository.count() == 0) {
                jobRepository.save(createJob(
                        "Vaga Interna 1",
                        "Atuação na manutenção e evolução de serviços internos.",
                        "Java, Spring Boot, SQL e Git.",
                        6,
                        admin
                ));

                jobRepository.save(createJob(
                        "Vaga Interna 2",
                        "Atuação na construção e manutenção de interfaces web.",
                        "Angular, TypeScript, HTML e CSS.",
                        12,
                        admin
                ));
            }
        };
    }

    private UserAccount createUser(String name,
                                   String email,
                                   String password,
                                   Role role,
                                   LocalDate hireDate,
                                   PasswordEncoder passwordEncoder) {
        UserAccount user = new UserAccount();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setHireDate(hireDate);
        user.setActive(true);
        return user;
    }

    private Job createJob(String title,
                          String description,
                          String requirements,
                          int minCompanyTime,
                          UserAccount createdBy) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setMinCompanyTime(minCompanyTime);
        job.setStatus(JobStatus.OPEN);
        job.setCreatedBy(createdBy);
        return job;
    }
}
