package com.za.testexe.gestioneStipendio.service;

import com.za.testexe.gestioneStipendio.model.dto.request.rate.RateRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.rate.RateResponse;

import java.util.List;

public interface RateService {
    RateResponse saveRate(RateRequest request);

    List<RateResponse> getRate();

    void deleteRate(Integer idRate);
}
