package com.za.testexe.gestioneStipendio.repository;

import com.za.testexe.gestioneStipendio.model.entity.SpeseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeseRepository extends JpaRepository<SpeseEntity, Integer> {
}
