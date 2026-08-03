package com.pacto.recrutamento.service;

import com.pacto.recrutamento.domain.Job;
import com.pacto.recrutamento.domain.UserAccount;
import com.pacto.recrutamento.domain.enums.JobStatus;
import com.pacto.recrutamento.dto.job.JobRequest;
import com.pacto.recrutamento.dto.job.JobResponse;
import com.pacto.recrutamento.exception.NotFoundException;
import com.pacto.recrutamento.repository.JobRepository;
import com.pacto.recrutamento.repository.spec.JobSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository repository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<JobResponse> search(String term, JobStatus status, Authentication authentication) {
        UserAccount user = currentUserService.require(authentication);
        return repository.findAll(
                        JobSpecifications.textContains(term).and(JobSpecifications.hasStatus(status)),
                        Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(job -> new JobResponse(job, user)).toList();
    }

    public Job require(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vaga não encontrada."));
    }

    @Transactional(readOnly = true)
    public JobResponse find(Long id, Authentication authentication) {
        return new JobResponse(require(id), currentUserService.require(authentication));
    }

    private void apply(Job job, JobRequest request) {
        job.setTitle(request.title().trim());
        job.setDescription(request.description().trim());
        job.setRequirements(request.requirements().trim());
        job.setMinCompanyTime(request.minMonthsAtCompany());
        job.setStatus(request.status());
    }

    @Transactional
    public JobResponse create(JobRequest request, Authentication authentication) {
        UserAccount admin = currentUserService.require(authentication);
        Job job = new Job();
        apply(job, request);
        job.setCreatedBy(admin);
        return new JobResponse(repository.save(job), admin);
    }

    @Transactional
    public JobResponse update(Long id, JobRequest request, Authentication authentication) {
        Job job = require(id);
        apply(job, request);
        return new JobResponse(repository.save(job), currentUserService.require(authentication));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(require(id));
    }


}
