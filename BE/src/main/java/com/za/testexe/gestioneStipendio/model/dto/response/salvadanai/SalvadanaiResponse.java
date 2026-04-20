package com.za.testexe.gestioneStipendio.model.dto.response.salvadanai;

import java.math.BigDecimal;

public record SalvadanaiResponse(
        Integer idSalvadanaio,
        String descrizione,
        BigDecimal euro,
        BigDecimal quotaFinale,
        BigDecimal euroAccumulati,
        Boolean attivo
) {
}
