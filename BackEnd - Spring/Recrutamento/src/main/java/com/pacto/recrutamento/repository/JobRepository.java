package com.pacto.recrutamento.repository;

import com.pacto.recrutamento.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository extends JpaRepository<Job, Long> , JpaSpecificationExecutor<Job> {
}
