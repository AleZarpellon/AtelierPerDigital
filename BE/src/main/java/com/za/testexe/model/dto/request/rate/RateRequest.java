package com.za.testexe.model.dto.request.rate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RateRequest(
        Integer idRate,

        @NotBlank(message = "Descrizione obbligatoria")
        String descrizione,

        @NotNull(message = "Euro obbligatorio")
        BigDecimal euro,

        Integer nrRate,
        Integer nrRateMax,
        BigDecimal maxValore,
        @NotBlank(message = "Periodo obbligatorio")
        String periodo,
        String mese,
        Boolean attivo
) {}
