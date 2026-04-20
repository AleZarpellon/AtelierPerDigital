package com.za.testexe.gestioneStipendio.mapper;

import com.za.testexe.gestioneStipendio.model.dto.request.spese.SpeseRequest;
import com.za.testexe.gestioneStipendio.model.dto.response.spese.SpeseResponse;
import com.za.testexe.gestioneStipendio.model.entity.SpeseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpeseMapper {
    SpeseEntity toEntity(SpeseRequest request);

    SpeseResponse toDto(SpeseEntity entity);

    @Mapping(target = "idSpesa", ignore = true)
    void updateEntity(SpeseRequest request, @MappingTarget SpeseEntity entity);
}
