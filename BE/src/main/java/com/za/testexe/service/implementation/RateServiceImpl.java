package com.za.testexe.service.implementation;

import com.za.testexe.mapper.RateMapper;
import com.za.testexe.model.dto.request.rate.RateRequest;
import com.za.testexe.model.dto.response.rate.RateResponse;
import com.za.testexe.model.entity.RateEntity;
import com.za.testexe.repository.RateRepository;
import com.za.testexe.service.RateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private static final String PERIODO_ANNUALE = "Annuale";

    private final RateRepository rateRepository;
    private final RateMapper rateMapper;

    @Override
    public RateResponse saveRate(RateRequest request) {
        RateEntity saved;
        if (request.idRate() == null || request.idRate() == 0) {
            RateEntity entity = rateMapper.toEntity(request);
            applyBusinessRules(request, entity, true);
            saved = rateRepository.save(entity);
        } else {
            RateEntity entity = rateRepository.findById(request.idRate())
                    .orElseThrow(() -> new EntityNotFoundException("Rata non trovata"));
            rateMapper.updateEntity(request, entity);
            applyBusinessRules(request, entity, false);
            saved = rateRepository.save(entity);
        }
        return rateMapper.toDto(saved);
    }

    @Override
    public List<RateResponse> getRate() {
        return rateRepository.findAllByOrderByPeriodoAsc()
                .stream()
                .map(rateMapper::toDto)
                .toList();
    }

    @Override
    public void deleteRate(Integer idRate) {
        RateEntity entity = rateRepository.findById(idRate)
                .orElseThrow(() -> new EntityNotFoundException("Rata non trovata"));
        rateRepository.delete(entity);
    }

    @Override
    public void updateAttivo(Integer idRate, Boolean attivo) {
        if (attivo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parametro attivo obbligatorio");
        }

        RateEntity entity = rateRepository.findById(idRate)
                .orElseThrow(() -> new EntityNotFoundException("Rata non trovata"));
        entity.setAttivo(attivo);
        rateRepository.save(entity);
    }

    private void applyBusinessRules(RateRequest request, RateEntity entity, boolean isCreate) {
        if (PERIODO_ANNUALE.equals(request.periodo())) {
            if (request.mese() == null || request.mese().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il mese e' obbligatorio per il periodo Annuale");
            }
            entity.setMese(request.mese());
        } else {
            entity.setMese(null);
        }

        if (isCreate && request.attivo() == null) {
            entity.setAttivo(Boolean.TRUE);
            return;
        }

        if (request.attivo() != null) {
            entity.setAttivo(request.attivo());
        }

        if (entity.getAttivo() == null) {
            entity.setAttivo(Boolean.TRUE);
        }
    }
}
