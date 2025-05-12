package com.woodenfurniture.c360mappingconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class C360MappingConfigRequest {
    private String permission;
    private String config;
}
