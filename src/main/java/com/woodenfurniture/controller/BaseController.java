package com.woodenfurniture.controller;

import com.woodenfurniture.common.BaseEntity;
import com.woodenfurniture.dto.request.BaseSearchRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.service.BaseService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BaseController<T extends BaseEntity, ID> {
    
    @PostMapping
    ResponseEntity<ApiResponse<?>> create(@RequestBody Object request);

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<?>> update(@PathVariable ID id, @RequestBody Object request);

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<?>> getById(@PathVariable ID id);

    @GetMapping("/uuid/{uuid}")
    ResponseEntity<ApiResponse<?>> getByUuid(@PathVariable String uuid);

    @GetMapping
    ResponseEntity<ApiResponse<?>> getAll();

    @GetMapping("/page")
    ResponseEntity<ApiResponse<Page<?>>> getAll(Pageable pageable);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable ID id);

    @DeleteMapping("/uuid/{uuid}")
    ResponseEntity<ApiResponse<Void>> deleteByUuid(@PathVariable String uuid);

    @GetMapping("/search")
    ResponseEntity<ApiResponse<Page<?>>> search(
            @RequestParam(required = false) BaseSearchRequest searchRequest,
            Pageable pageable);

    @PostMapping("/import")
    ResponseEntity<Resource> importData(@RequestParam("file") MultipartFile file);

    @GetMapping("/export")
    ResponseEntity<Resource> exportData(
            @RequestParam(required = false) BaseSearchRequest searchTerm,
            Pageable pageable);
} 