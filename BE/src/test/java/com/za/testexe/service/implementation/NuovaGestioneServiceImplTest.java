package com.za.testexe.service.implementation;

import com.za.testexe.model.entity.RateEntity;
import com.za.testexe.model.entity.StipendioEntity;
import com.za.testexe.repository.BudgetSettimanaleRepository;
import com.za.testexe.repository.RateRepository;
import com.za.testexe.repository.SalvadanaiRepository;
import com.za.testexe.repository.SpeseRepository;
import com.za.testexe.repository.StipendioRepository;
import com.za.testexe.service.RisparmioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NuovaGestioneServiceImplTest {

    @Mock
    private BudgetSettimanaleRepository budgetRepository;

    @Mock
    private SpeseRepository speseRepository;

    @Mock
    private SalvadanaiRepository salvadanaiRepository;

    @Mock
    private RateRepository rateRepository;

    @Mock
    private StipendioRepository stipendioRepository;

    @Mock
    private RisparmioService risparmioService;

    @InjectMocks
    private NuovaGestioneServiceImpl service;

    @BeforeEach
    void setupCommonMocks() {
        StipendioEntity stipendio = new StipendioEntity();
        stipendio.setNrSettimane(4);

        when(stipendioRepository.findAll()).thenReturn(List.of(stipendio));
        when(budgetRepository.findAll()).thenReturn(List.of());
        when(speseRepository.findAll()).thenReturn(List.of());
        when(salvadanaiRepository.findByAttivoTrue()).thenReturn(List.of());
        when(stipendioRepository.save(any(StipendioEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void nuovaGestione_shouldSetNonAnnualRatesAlwaysActive() {
        RateEntity rataMensile = RateEntity.builder()
                .idRate(1)
                .descrizione("Affitto")
                .euro(BigDecimal.valueOf(500))
                .nrRate(1)
                .nrRateMax(12)
                .maxValore(BigDecimal.valueOf(5500))
                .periodo("Inizio mese")
                .attivo(false)
                .build();

        when(rateRepository.findAll()).thenReturn(List.of(rataMensile));
        when(rateRepository.save(any(RateEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.nuovaGestione();

        ArgumentCaptor<RateEntity> captor = ArgumentCaptor.forClass(RateEntity.class);
        verify(rateRepository, atLeastOnce()).save(captor.capture());
        RateEntity salvata = captor.getAllValues().getLast();

        assertTrue(salvata.getAttivo());
        assertEquals(2, salvata.getNrRate());
    }

    @Test
    void nuovaGestione_shouldActivateAnnualRateOnlyInCurrentMonthWithoutIncrementingCounters() {
        String meseCorrente = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);

        RateEntity annualeCorrente = RateEntity.builder()
                .idRate(2)
                .descrizione("Bollo auto")
                .euro(BigDecimal.valueOf(200))
                .nrRate(0)
                .nrRateMax(5)
                .maxValore(BigDecimal.valueOf(1000))
                .periodo("Annuale")
                .mese(meseCorrente)
                .attivo(false)
                .build();

        RateEntity annualeAltroMese = RateEntity.builder()
                .idRate(3)
                .descrizione("Assicurazione")
                .euro(BigDecimal.valueOf(300))
                .nrRate(0)
                .nrRateMax(5)
                .maxValore(BigDecimal.valueOf(1500))
                .periodo("Annuale")
                .mese("Gennaio")
                .attivo(true)
                .build();

        if ("Gennaio".equalsIgnoreCase(meseCorrente)) {
            annualeAltroMese.setMese("Febbraio");
        }

        when(rateRepository.findAll()).thenReturn(List.of(annualeCorrente, annualeAltroMese));
        when(rateRepository.save(any(RateEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.nuovaGestione();

        assertTrue(annualeCorrente.getAttivo());
        assertFalse(annualeAltroMese.getAttivo());
        assertEquals(0, annualeCorrente.getNrRate());
        assertEquals(0, annualeAltroMese.getNrRate());
    }

    @Test
    void nuovaGestione_shouldUpdateAttivoEvenWhenInstallmentCountersAreMissing() {
        String meseCorrente = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);

        RateEntity annualeSenzaContatori = RateEntity.builder()
                .idRate(4)
                .descrizione("Abbonamento annuale")
                .euro(BigDecimal.valueOf(120))
                .nrRate(null)
                .nrRateMax(null)
                .periodo("Annuale")
                .mese(meseCorrente)
                .attivo(false)
                .build();

        when(rateRepository.findAll()).thenReturn(List.of(annualeSenzaContatori));
        when(rateRepository.save(any(RateEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.nuovaGestione();

        assertTrue(annualeSenzaContatori.getAttivo());
        assertNull(annualeSenzaContatori.getNrRate());
        assertNull(annualeSenzaContatori.getNrRateMax());
    }
}

