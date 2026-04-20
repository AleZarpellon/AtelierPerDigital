package com.za.testexe.gestioneStipendio.model.dto.response.risparmio;

import java.math.BigDecimal;

public record RisparmioResponse(
        Integer idRisparmio,
        String descrizione,
        BigDecimal euro,
        String periodo
) {
}
