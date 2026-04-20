package com.za.testexe.gestioneStipendio.service;

import com.za.testexe.gestioneStipendio.model.dto.request.risparmio.RisparmioRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.risparmio.RisparmioResponse;

import java.math.BigDecimal;
import java.util.List;

public interface RisparmioService {
    List<RisparmioResponse> getRisparmi();

    void saveRisparmio(RisparmioRequest request);

    public void aggiornaTotale(BigDecimal importo);
}
