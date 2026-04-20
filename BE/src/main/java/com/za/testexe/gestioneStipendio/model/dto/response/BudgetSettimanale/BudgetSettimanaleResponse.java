package com.za.testexe.gestioneStipendio.model.dto.response.BudgetSettimanale;

import java.math.BigDecimal;

public record BudgetSettimanaleResponse(
        Integer idSettimana,
        BigDecimal soldiXSettimana,
        BigDecimal speso,
        BigDecimal rimanente
) {
}
