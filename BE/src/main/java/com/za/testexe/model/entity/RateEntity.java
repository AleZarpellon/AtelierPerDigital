package com.za.testexe.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "rate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRate;
    private String descrizione;
    private BigDecimal euro;
    private Integer nrRate;
    private Integer nrRateMax;
    private BigDecimal maxValore;
    private String periodo;

    @Column(nullable = true)
    private String mese;

    @Column(nullable = false)
    @Builder.Default
    private Boolean attivo = Boolean.TRUE;
}
