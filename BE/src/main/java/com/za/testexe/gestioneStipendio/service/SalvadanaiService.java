package com.za.testexe.gestioneStipendio.service;

import com.za.testexe.gestioneStipendio.model.dto.request.salvadanai.SalvadanaiRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.salvadanai.SalvadanaiResponse;

import java.math.BigDecimal;
import java.util.List;

public interface SalvadanaiService {
    SalvadanaiResponse saveSalvadanaio(SalvadanaiRequest request);

    List<SalvadanaiResponse> getSalvadanai();

    void deleteSalvadanaio(Integer idSalvadanaio);

    void updateAttivo(Integer idSalvadanaio, Boolean attivo);

    void accumula(Integer idSalvadanaio, BigDecimal euro);
}
