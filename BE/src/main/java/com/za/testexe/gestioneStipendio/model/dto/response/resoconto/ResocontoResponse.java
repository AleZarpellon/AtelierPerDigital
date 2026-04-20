package com.za.testexe.gestioneStipendio.model.dto.response.resoconto;

import java.math.BigDecimal;

public record ResocontoResponse(
        BigDecimal stipendio,
        Integer nrSettimane,
        BigDecimal totSpese,
        BigDecimal totRate,
        BigDecimal totSalvadanai,
        BigDecimal totale,
        BigDecimal rimanente
) {
}
