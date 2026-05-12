package com.za.testexe.mapper;

import com.za.testexe.model.dto.request.rate.RateRequest;
import com.za.testexe.model.dto.response.rate.RateResponse;
import com.za.testexe.model.entity.RateEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RateMapper {
    RateEntity toEntity(RateRequest request);

    RateResponse toDto(RateEntity entity);

    @Mapping(target = "idRate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(RateRequest request, @MappingTarget RateEntity entity);
}
