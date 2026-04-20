package com.za.testexe.gestioneStipendio.controller;

import com.za.testexe.gestioneStipendio.controller.API.RateAPI;
import com.za.testexe.gestioneStipendio.model.dto.request.rate.RateRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.common.ApiResponse;
import com.za.testexe.gestioneStipendio.model.dto.response.rate.RateResponse;
import com.za.testexe.gestioneStipendio.service.RateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RateController implements RateAPI {

    private final RateService rateService;

    @Override
    public ResponseEntity<ApiResponse<RateResponse>> saveRate(@Valid @RequestBody RateRequest request) {
        RateResponse result = rateService.saveRate(request);
        return ResponseEntity.ok(ApiResponse.success("Rata salvata correttamente", result));
    }

    @Override
    public ResponseEntity<ApiResponse<List<RateResponse>>> getRate() {
        return ResponseEntity.ok(ApiResponse.success("Lista recuperata", rateService.getRate()));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteRate(Integer idRate) {
        rateService.deleteRate(idRate);
        return ResponseEntity.ok(ApiResponse.success("Rata eliminata correttamente"));
    }
}
