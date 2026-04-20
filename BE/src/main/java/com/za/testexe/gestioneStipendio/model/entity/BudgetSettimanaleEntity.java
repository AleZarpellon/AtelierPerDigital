package com.za.testexe.gestioneStipendio.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "budget_settimanale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BudgetSettimanaleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSettimana;
    private BigDecimal soldiXSettimana;
    private BigDecimal speso;
    private BigDecimal rimanente;
}
