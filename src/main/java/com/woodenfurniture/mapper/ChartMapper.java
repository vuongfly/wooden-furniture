package com.woodenfurniture.mapper;

import com.woodenfurniture.dto.ChartRequest;
import com.woodenfurniture.dto.response.ChartResponse;
import com.woodenfurniture.entity.Chart;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChartMapper {
    
    /**
     * Maps a ChartRequest to a new Chart entity
     */
    Chart toEntity(ChartRequest request);
    
    /**
     * Updates an existing Chart entity with data from ChartRequest
     */
    void updateEntity(@MappingTarget Chart chart, ChartRequest request);
    
    /**
     * Maps a Chart entity to ChartResponse
     */
    ChartResponse toResponse(Chart chart);
} 