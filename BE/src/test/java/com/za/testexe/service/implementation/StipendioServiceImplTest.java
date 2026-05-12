package com.za.testexe.service.implementation;

import com.za.testexe.mapper.StipendioMapper;
import com.za.testexe.model.dto.response.resoconto.ResocontoResponse;
import com.za.testexe.model.entity.RateEntity;
import com.za.testexe.model.entity.SalvadanaiEntity;
import com.za.testexe.model.entity.SpeseEntity;
import com.za.testexe.model.entity.StipendioEntity;
import com.za.testexe.repository.RateRepository;
import com.za.testexe.repository.SalvadanaiRepository;
import com.za.testexe.repository.SpeseRepository;
import com.za.testexe.repository.StipendioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StipendioServiceImplTest {

    @Mock
    private StipendioRepository stipendioRepository;

    @Mock
    private SpeseRepository speseRepository;

    @Mock
    private RateRepository rateRepository;

    @Mock
    private SalvadanaiRepository salvadanaiRepository;

    @Mock
    private StipendioMapper stipendioMapper;

    @Mock
    private BudgetSettimanaleServiceImpl budgetService;

    @InjectMocks
    private StipendioServiceImpl service;

    @Test
    void getResoconto_shouldSumOnlyActiveRates() {
        StipendioEntity stipendio = new StipendioEntity();
        stipendio.setStipendio(BigDecimal.valueOf(2000));
        stipendio.setNrSettimane(4);

        SpeseEntity spesa = new SpeseEntity();
        spesa.setEuro(BigDecimal.valueOf(100));

        RateEntity rataAttiva = RateEntity.builder()
                .euro(BigDecimal.valueOf(300))
                .attivo(true)
                .build();

        RateEntity rataDisattiva = RateEntity.builder()
                .euro(BigDecimal.valueOf(500))
                .attivo(false)
                .build();

        SalvadanaiEntity salvadanaioAttivo = new SalvadanaiEntity();
        salvadanaioAttivo.setEuro(BigDecimal.valueOf(200));
        salvadanaioAttivo.setAttivo(true);

        when(stipendioRepository.findAll()).thenReturn(List.of(stipendio));
        when(speseRepository.findAll()).thenReturn(List.of(spesa));
        when(rateRepository.findAll()).thenReturn(List.of(rataAttiva, rataDisattiva));
        when(salvadanaiRepository.findAll()).thenReturn(List.of(salvadanaioAttivo));

        ResocontoResponse response = service.getResoconto();

        assertEquals(BigDecimal.valueOf(100), response.totSpese());
        assertEquals(BigDecimal.valueOf(300), response.totRate());
        assertEquals(BigDecimal.valueOf(200), response.totSalvadanai());
        assertEquals(BigDecimal.valueOf(600), response.totale());
        assertEquals(BigDecimal.valueOf(1400), response.rimanente());

        verify(budgetService).generaBudgetSettimanale(any(BigDecimal.class), any(Integer.class));
    }
}

