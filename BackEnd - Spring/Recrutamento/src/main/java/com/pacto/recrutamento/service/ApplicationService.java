package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.Application;
import com.pacto.recrutamento.domain.Job;
import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.ApplicationStatus;
import com.pacto.recrutamento.domain.enums.JobStatus;
import com.pacto.recrutamento.dto.application.ApplicationResponse;
import com.pacto.recrutamento.dto.application.UpdateApplicationStatusRequest;
import com.pacto.recrutamento.exception.BusinessException;
import com.pacto.recrutamento.exception.NotFoundException;
import com.pacto.recrutamento.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;
    private final JobService jobService;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;


    @Transactional
    public ApplicationResponse apply(Long jobId, Authentication authentication) {
        UserAccount candidate = currentUserService.require(authentication);
        Job job = jobService.require(jobId);

        if (job.getStatus() != JobStatus.OPEN) {
            throw new BusinessException("Esta vaga não está aberta para candidaturas.");
        }
        if (candidate.monthsAtCompany() < job.getMinCompanyTime()) {
            throw new BusinessException("Você ainda não atende ao tempo mínimo de empresa exigido.");
        }
        if (repository.existsByCandidateIdAndJobId(candidate.getId(), jobId)) {
            throw new BusinessException("Você já se candidatou a esta vaga.");
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(ApplicationStatus.SUBMITTED);
        repository.save(application);

        notificationService.create(candidate, "Candidatura enviada",
                "Sua candidatura para “" + job.getTitle() + "” foi registrada.");
        notificationService.create(job.getCreatedBy(), "Nova candidatura",
                candidate.getName() + " candidatou-se à vaga “" + job.getTitle() + "”.");

        return new ApplicationResponse(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> mine(Authentication authentication) {
        UserAccount candidate = currentUserService.require(authentication);
        return repository.findByCandidateIdOrderByCreatedAtDesc(candidate.getId())
                .stream().map(ApplicationResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> byJob(Long jobId, Integer minMonthsAtCompany) {
        jobService.require(jobId);
        int minMonths = minMonthsAtCompany == null ? 0 : Math.max(0, minMonthsAtCompany);
        return repository.findByJobIdOrderByCreatedAtDesc(jobId).stream()
                .filter(application -> application.getCandidate().monthsAtCompany() >= minMonths)
                .map(ApplicationResponse::new)
                .toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, UpdateApplicationStatusRequest request) {
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Candidatura não encontrada."));
        application.setStatus(request.status());
        application.setFeedback(request.feedback() == null ? null : request.feedback().trim());

        String message = "A candidatura para “" + application.getJob().getTitle() + "” está como " + request.status().getDescription() + ".";
        if (application.getFeedback() != null && !application.getFeedback().isBlank()) {
            message += " Feedback: " + application.getFeedback();
        }

        notificationService.create(application.getCandidate(),"Status da candidatura atualizado",message);
        return new ApplicationResponse(application);
    }
}
