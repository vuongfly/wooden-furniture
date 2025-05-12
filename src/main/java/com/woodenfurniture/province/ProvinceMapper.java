package com.woodenfurniture.province;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProvinceMapper {

    /**
     * Maps a Province entity to a ProvinceResponse DTO
     */
    ProvinceResponse toResponse(Province entity);

    /**
     * Maps a ProvinceRequest to a Province entity
     */
    Province toEntity(ProvinceRequest request);

    /**
     * Updates a Province entity from a ProvinceRequest
     */
    void updateEntityFromRequest(ProvinceRequest request, @MappingTarget Province entity);
}
