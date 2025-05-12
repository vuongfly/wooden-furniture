package com.woodenfurniture.c360mappingconfig;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface C360MappingConfigMapper {

    /**
     * Maps a C360MappingConfig entity to a C360MappingConfigResponse DTO
     */
    C360MappingConfigResponse toResponse(C360MappingConfig entity);

    /**
     * Maps a C360MappingConfigRequest to a C360MappingConfig entity
     */
    C360MappingConfig toEntity(C360MappingConfigRequest request);

    /**
     * Updates a C360MappingConfig entity from a C360MappingConfigRequest
     */
    void updateEntityFromRequest(C360MappingConfigRequest request, @MappingTarget C360MappingConfig entity);
}
