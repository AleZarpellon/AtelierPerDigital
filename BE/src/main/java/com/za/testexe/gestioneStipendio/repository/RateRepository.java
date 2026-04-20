package com.za.testexe.gestioneStipendio.repository;

import com.za.testexe.gestioneStipendio.model.entity.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<RateEntity, Integer> {
    List<RateEntity> findAllByOrderByPeriodoAsc();
}
