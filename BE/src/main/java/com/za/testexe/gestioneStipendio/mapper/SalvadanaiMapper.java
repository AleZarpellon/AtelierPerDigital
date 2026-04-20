package com.za.testexe.gestioneStipendio.mapper;

import com.za.testexe.gestioneStipendio.model.dto.request.salvadanai.SalvadanaiRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.salvadanai.SalvadanaiResponse;
import com.za.testexe.gestioneStipendio.model.entity.SalvadanaiEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SalvadanaiMapper {

    SalvadanaiEntity toEntity(SalvadanaiRequest request);

    SalvadanaiResponse toDto(SalvadanaiEntity entity);

    @Mapping(target = "idSalvadanaio", ignore = true)
    void updateEntity(SalvadanaiRequest request, @MappingTarget SalvadanaiEntity entity);
}
