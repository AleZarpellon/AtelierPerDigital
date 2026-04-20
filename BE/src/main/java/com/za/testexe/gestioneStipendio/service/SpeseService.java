package com.za.testexe.gestioneStipendio.service;

import com.za.testexe.gestioneStipendio.model.dto.request.spese.SpeseRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.spese.SpeseResponse;

import java.util.List;

public interface SpeseService {
    SpeseResponse saveSpesa(SpeseRequest request);

    List<SpeseResponse> getSpese();

    void deleteSpesa(Integer idSpesa);
}
