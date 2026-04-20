package com.za.testexe.gestioneStipendio.controller;

import com.za.testexe.gestioneStipendio.controller.API.SpeseAPI;
import com.za.testexe.gestioneStipendio.model.dto.request.spese.SpeseRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.spese.SpeseResponse;
import com.za.testexe.gestioneStipendio.model.dto.response.common.ApiResponse;
import com.za.testexe.gestioneStipendio.service.SpeseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpeseController implements SpeseAPI {

    private final SpeseService speseService;

    @Override
    public ResponseEntity<ApiResponse<SpeseResponse>> saveSpesa(@Valid @RequestBody SpeseRequest request) {
        System.out.println("Request: " + request);
        SpeseResponse result = speseService.saveSpesa(request);
        return ResponseEntity.ok(ApiResponse.success("Spesa salvata correttamente", result));
    }

    @Override
    public ResponseEntity<ApiResponse<List<SpeseResponse>>> getSpese() {
        return ResponseEntity.ok(ApiResponse.success("Lista recuperata", speseService.getSpese()));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteSpesa(Integer idSpesa) {
        speseService.deleteSpesa(idSpesa);
        return ResponseEntity.ok(ApiResponse.success("Spesa eliminata correttamente"));
    }


}
