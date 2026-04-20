package com.za.testexe.gestioneStipendio.service;

import com.za.testexe.gestioneStipendio.model.dto.request.stipendio.StipendioRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.resoconto.ResocontoResponse;
import com.za.testexe.gestioneStipendio.model.dto.response.stipendio.StipendioResponse;

public interface StipendioService {
    StipendioResponse saveStipendio(StipendioRequest request);

    StipendioResponse getStipendio();

    ResocontoResponse getResoconto();
}
