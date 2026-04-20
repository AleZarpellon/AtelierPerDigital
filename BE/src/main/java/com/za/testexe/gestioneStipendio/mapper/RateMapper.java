package com.za.testexe.gestioneStipendio.mapper;

import com.za.testexe.gestioneStipendio.model.dto.request.rate.RateRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.rate.RateResponse;
import com.za.testexe.gestioneStipendio.model.entity.RateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RateMapper {
    RateEntity toEntity(RateRequest request);

    RateResponse toDto(RateEntity entity);

    @Mapping(target = "idRate", ignore = true)
    void updateEntity(RateRequest request, @MappingTarget RateEntity entity);
}
