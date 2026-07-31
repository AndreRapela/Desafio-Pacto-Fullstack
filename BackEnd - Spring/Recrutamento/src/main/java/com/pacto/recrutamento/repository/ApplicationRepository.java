package com.pacto.recrutamento.repository;

import com.pacto.recrutamento.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository  extends JpaRepository<Application, Long> {

}
