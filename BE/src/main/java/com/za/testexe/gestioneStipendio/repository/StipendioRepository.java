package com.za.testexe.gestioneStipendio.repository;

import com.za.testexe.gestioneStipendio.model.entity.StipendioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StipendioRepository extends JpaRepository<StipendioEntity, Integer> {
}
