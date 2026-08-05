package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.Application;
import com.pacto.recrutamento.domain.Job;
import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.ApplicationStatus;
import com.pacto.recrutamento.domain.enums.JobStatus;
import com.pacto.recrutamento.dto.application.UpdateApplicationStatusRequest;
import com.pacto.recrutamento.exception.BusinessException;
import com.pacto.recrutamento.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository repository;

    @Mock
    private JobService jobService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationService service;

    @Test
    void shouldRejectApplicationForClosedJob() {
        Long jobId = 1L;
        UserAccount candidate = candidate(LocalDate.now().minusYears(2));
        Job job = job(JobStatus.CLOSED, 0);
        var authentication = authentication(candidate);

        when(currentUserService.require(authentication)).thenReturn(candidate);
        when(jobService.require(jobId)).thenReturn(job);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.apply(jobId, authentication)
        );

        assertEquals(
                "Esta vaga não está aberta para candidaturas.",
                exception.getMessage()
        );
        verify(repository, never()).save(any(Application.class));
    }

    @Test
    void shouldRejectDuplicateApplication() {
        Long jobId = 1L;
        UserAccount candidate = candidate(LocalDate.now().minusYears(2));
        Job job = job(JobStatus.OPEN, 6);
        var authentication = authentication(candidate);

        when(currentUserService.require(authentication)).thenReturn(candidate);
        when(jobService.require(jobId)).thenReturn(job);
        when(repository.existsByCandidateIdAndJobId(candidate.getId(), jobId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> service.apply(jobId, authentication)
        );

        verify(repository, never()).save(any(Application.class));
    }

    @Test
    void shouldRejectCandidateWithoutRequiredTenure() {
        Long jobId = 1L;
        UserAccount candidate = candidate(LocalDate.now().minusMonths(3));
        Job job = job(JobStatus.OPEN, 12);
        var authentication = authentication(candidate);

        when(currentUserService.require(authentication)).thenReturn(candidate);
        when(jobService.require(jobId)).thenReturn(job);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.apply(jobId, authentication)
        );

        assertEquals(
                "Você ainda não atende ao tempo mínimo de empresa exigido.",
                exception.getMessage()
        );
        verify(repository, never()).save(any(Application.class));
    }

    @Test
    void shouldCreateApplicationAndNotifyBothUsers() {
        Long jobId = 1L;
        UserAccount candidate = candidate(LocalDate.now().minusYears(2));
        UserAccount admin = user(2L, "Administrador", "admin@admin.com");
        Job job = job(JobStatus.OPEN, 6);
        job.setCreatedBy(admin);
        var authentication = authentication(candidate);

        when(currentUserService.require(authentication)).thenReturn(candidate);
        when(jobService.require(jobId)).thenReturn(job);
        when(repository.existsByCandidateIdAndJobId(candidate.getId(), jobId))
                .thenReturn(false);
        when(repository.save(any(Application.class))).thenAnswer(invocation -> {
            Application application = invocation.getArgument(0);
            application.setId(1L);
            return application;
        });

        var response = service.apply(jobId, authentication);

        ArgumentCaptor<Application> captor =
                ArgumentCaptor.forClass(Application.class);

        verify(repository).save(captor.capture());

        Application saved = captor.getValue();

        assertEquals(candidate, saved.getCandidate());
        assertEquals(job, saved.getJob());
        assertEquals(ApplicationStatus.SUBMITTED, saved.getStatus());
        assertEquals("Vaga Interna 1", response.jobTitle());

        verify(notificationService, times(2)).create(
                any(UserAccount.class),
                any(String.class),
                any(String.class)
        );
    }

    @Test
    void shouldFilterApplicationsByCandidateTenure() {
        Long jobId = 1L;
        Job job = job(JobStatus.OPEN, 0);

        UserAccount candidateWithTwoYears =
                candidate(LocalDate.now().minusYears(2));

        UserAccount candidateWithThreeMonths =
                candidate(LocalDate.now().minusMonths(3));

        when(jobService.require(jobId)).thenReturn(job);
        when(repository.findByJobIdOrderByCreatedAtDesc(jobId))
                .thenReturn(List.of(
                        application(1L, job, candidateWithTwoYears),
                        application(2L, job, candidateWithThreeMonths)
                ));

        var result = service.byJob(jobId, 12);

        assertEquals(1, result.size());
        assertEquals("Candidato", result.getFirst().candidateName());
    }

    @Test
    void shouldUpdateStatusTrimFeedbackAndNotifyCandidate() {
        Long applicationId = 1L;
        UserAccount candidate = candidate(LocalDate.now().minusYears(2));
        Job job = job(JobStatus.OPEN, 0);
        Application application = application(applicationId, job, candidate);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.UNDER_REVIEW,
                        "  Perfil em análise  "
                );

        when(repository.findById(applicationId))
                .thenReturn(Optional.of(application));

        var response = service.updateStatus(applicationId, request);

        assertEquals(ApplicationStatus.UNDER_REVIEW, response.status());
        assertEquals("Perfil em análise", response.feedback());

        ArgumentCaptor<String> messageCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(notificationService).create(
                eq(candidate),
                eq("Status da candidatura atualizado"),
                messageCaptor.capture()
        );

        assertTrue(messageCaptor.getValue().contains("Vaga Interna 1"));
    }

    private UsernamePasswordAuthenticationToken authentication(
            UserAccount candidate
    ) {
        return new UsernamePasswordAuthenticationToken(
                candidate.getEmail(),
                null
        );
    }

    private UserAccount candidate(LocalDate hireDate) {
        UserAccount candidate = user(
                1L,
                "Candidato",
                "candidato@candidato.com"
        );
        candidate.setHireDate(hireDate);
        return candidate;
    }

    private UserAccount user(Long id, String name, String email) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setHireDate(LocalDate.now().minusYears(1));
        user.setActive(true);
        return user;
    }

    private Job job(JobStatus status, int minCompanyTime) {
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Vaga Interna 1");
        job.setDescription("Descrição da vaga");
        job.setRequirements("Java e Angular");
        job.setStatus(status);
        job.setMinCompanyTime(minCompanyTime);
        job.setCreatedBy(user(2L, "Administrador", "admin@admin.com"));
        return job;
    }

    private Application application(
            Long id,
            Job job,
            UserAccount candidate
    ) {
        Application application = new Application();
        application.setId(id);
        application.setJob(job);
        application.setCandidate(candidate);
        application.setStatus(ApplicationStatus.SUBMITTED);
        return application;
    }
}
