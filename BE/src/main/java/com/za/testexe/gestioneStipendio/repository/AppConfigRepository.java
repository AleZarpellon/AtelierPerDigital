package com.za.testexe.gestioneStipendio.repository;

import com.za.testexe.gestioneStipendio.model.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, String> {
}
