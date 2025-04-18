package com.woodenfurniture.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapstructConfig {
    // Configuration interface for MapStruct
}
