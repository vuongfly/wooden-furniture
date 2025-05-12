package com.woodenfurniture.c360mappingconfig;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface C360MappingConfigBaseController {

    @GetMapping
    ResponseEntity<List<C360MappingConfigResponse>> findAll();

    @GetMapping("/{id}")
    ResponseEntity<C360MappingConfigResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<C360MappingConfigResponse> create(@RequestBody C360MappingConfigRequest request);

    @PutMapping("/{id}")
    ResponseEntity<C360MappingConfigResponse> update(@PathVariable Long id, @RequestBody C360MappingConfigRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/permission/{permission}")
    ResponseEntity<C360MappingConfigResponse> findByPermission(@PathVariable String permission);
    
    @GetMapping("/search")
    ResponseEntity<List<C360MappingConfigResponse>> searchConfigs(@RequestParam String searchTerm);
}
