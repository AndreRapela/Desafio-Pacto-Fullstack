package com.pacto.recrutamento.repository;

import com.pacto.recrutamento.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
