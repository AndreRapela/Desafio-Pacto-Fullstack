package com.pacto.recrutamento.repository;

import com.pacto.recrutamento.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ApplicationRepository  extends JpaRepository<Application, Long> {
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
    List<Application> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);
    List<Application> findByJobIdOrderByCreatedAtDesc(Long jobId);
}
