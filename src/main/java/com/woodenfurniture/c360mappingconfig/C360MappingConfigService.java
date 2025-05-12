package com.woodenfurniture.c360mappingconfig;

import java.util.List;
import java.util.Optional;

public interface C360MappingConfigService {
    List<C360MappingConfigResponse> findAll();
    C360MappingConfigResponse findById(Long id);
    C360MappingConfigResponse create(C360MappingConfigRequest request);
    C360MappingConfigResponse update(Long id, C360MappingConfigRequest request);
    void delete(Long id);
    Optional<C360MappingConfigResponse> findByPermission(String permission);
    List<C360MappingConfigResponse> searchConfigs(String searchTerm);
}
