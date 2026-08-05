package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.Job;
import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.JobStatus;
import com.pacto.recrutamento.domain.enums.Role;
import com.pacto.recrutamento.dto.job.JobRequest;
import com.pacto.recrutamento.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository repository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private JobService service;

    @Test
    void shouldCreateJobWithTrimmedFieldsAndCurrentAdmin() {
        UserAccount admin = user(
                1L,
                "Administrador",
                "admin@admin.com",
                Role.ADMIN,
                LocalDate.now().minusYears(4)
        );

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        admin.getEmail(),
                        null
                );

        JobRequest request = new JobRequest(
                "  Vaga Interna 1  ",
                "  Descrição da vaga  ",
                "  Java e Angular  ",
                6,
                JobStatus.OPEN
        );

        when(currentUserService.require(authentication))
                .thenReturn(admin);

        when(repository.save(any(Job.class)))
                .thenAnswer(invocation -> {
                    Job job = invocation.getArgument(0);
                    job.setId(1L);
                    return job;
                });

        var response = service.create(request, authentication);

        ArgumentCaptor<Job> jobCaptor =
                ArgumentCaptor.forClass(Job.class);

        verify(repository).save(jobCaptor.capture());

        Job saved = jobCaptor.getValue();

        assertEquals("Vaga Interna 1", saved.getTitle());
        assertEquals("Descrição da vaga", saved.getDescription());
        assertEquals("Java e Angular", saved.getRequirements());
        assertEquals(6, saved.getMinCompanyTime());
        assertEquals(admin, saved.getCreatedBy());
        assertEquals("Administrador", response.createdBy());
    }

    @Test
    void shouldInformEligibilityBasedOnCompanyTenure() {
        UserAccount candidate = user(
                1L,
                "Candidato",
                "candidato@candidato.com",
                Role.CANDIDATE,
                LocalDate.now().minusYears(2)
        );

        UserAccount admin = user(
                2L,
                "Administrador",
                "admin@admin.com",
                Role.ADMIN,
                LocalDate.now().minusYears(4)
        );

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Vaga Interna 1");
        job.setDescription("Descrição");
        job.setRequirements("Java e Angular");
        job.setMinCompanyTime(12);
        job.setStatus(JobStatus.OPEN);
        job.setCreatedBy(admin);

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        candidate.getEmail(),
                        null
                );

        when(repository.findById(job.getId()))
                .thenReturn(Optional.of(job));
        when(currentUserService.require(authentication))
                .thenReturn(candidate);

        var response = service.find(job.getId(), authentication);

        assertTrue(response.eligible());
    }

    private UserAccount user(
            Long id,
            String name,
            String email,
            Role role,
            LocalDate hireDate
    ) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setHireDate(hireDate);
        user.setActive(true);
        return user;
    }
}
