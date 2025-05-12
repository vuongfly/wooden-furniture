package com.woodenfurniture.c360mappingconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class C360MappingConfigControllerImpl implements C360MappingConfigController {

    private final C360MappingConfigService c360MappingConfigService;

    @Autowired
    public C360MappingConfigControllerImpl(C360MappingConfigService c360MappingConfigService) {
        this.c360MappingConfigService = c360MappingConfigService;
    }

    @Override
    public ResponseEntity<List<C360MappingConfigResponse>> findAll() {
        return ResponseEntity.ok(c360MappingConfigService.findAll());
    }

    @Override
    public ResponseEntity<C360MappingConfigResponse> findById(Long id) {
        try {
            return ResponseEntity.ok(c360MappingConfigService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<C360MappingConfigResponse> create(C360MappingConfigRequest request) {
        C360MappingConfigResponse response = c360MappingConfigService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<C360MappingConfigResponse> update(Long id, C360MappingConfigRequest request) {
        try {
            return ResponseEntity.ok(c360MappingConfigService.update(id, request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        try {
            c360MappingConfigService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<C360MappingConfigResponse> findByPermission(String permission) {
        return c360MappingConfigService.findByPermission(permission)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<List<C360MappingConfigResponse>> searchConfigs(String searchTerm) {
        List<C360MappingConfigResponse> configs = c360MappingConfigService.searchConfigs(searchTerm);
        return ResponseEntity.ok(configs);
    }
}
