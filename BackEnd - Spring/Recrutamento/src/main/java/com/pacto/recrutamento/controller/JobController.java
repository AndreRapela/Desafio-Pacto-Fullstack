package com.pacto.recrutamento.controller;

import com.pacto.recrutamento.domain.enums.JobStatus;
import com.pacto.recrutamento.dto.job.JobRequest;
import com.pacto.recrutamento.dto.job.JobResponse;
import com.pacto.recrutamento.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService service;

    @GetMapping
    public List<JobResponse> search(@RequestParam(required = false) String term,
                                    @RequestParam(required = false) JobStatus status,
                                    Authentication authentication) {
        return service.search(term, status, authentication);
    }

    @GetMapping("/{id}")
    public JobResponse find(@PathVariable Long id, Authentication authentication) {
        return service.find(id, authentication);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@Valid @RequestBody JobRequest request, Authentication authentication) {
        return service.create(request, authentication);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public JobResponse update(@PathVariable Long id, @Valid @RequestBody JobRequest request,
                              Authentication authentication) {
        return service.update(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}


