package com.pacto.recrutamento.controller;

import com.pacto.recrutamento.dto.application.ApplicationResponse;
import com.pacto.recrutamento.dto.application.UpdateApplicationStatusRequest;
import com.pacto.recrutamento.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService service;

    @PostMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(@PathVariable Long jobId, Authentication authentication) {
        return service.apply(jobId, authentication);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    public List<ApplicationResponse> mine(Authentication authentication) {
        return service.mine(authentication);
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApplicationResponse> byJob(@PathVariable Long jobId,
                                           @RequestParam(required = false) Integer minMonthsAtCompany) {
        return service.byJob(jobId, minMonthsAtCompany);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApplicationResponse updateStatus(@PathVariable Long id,
                                            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        return service.updateStatus(id, request);
    }
}
