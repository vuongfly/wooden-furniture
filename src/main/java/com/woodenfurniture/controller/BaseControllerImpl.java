package com.woodenfurniture.controller;

import com.woodenfurniture.common.BaseEntity;
import com.woodenfurniture.dto.request.BaseSearchRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.service.BaseService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseControllerImpl<T extends BaseEntity, ID> implements BaseController<T, ID> {

    @Getter
    protected final BaseService<T, ID> service;
    protected final String entityName;

    @Override
    public ResponseEntity<ApiResponse<?>> create(@RequestBody Object request) {
        Object response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .code(HttpStatus.CREATED.value())
                        .message(entityName + " created successfully")
                        .result(response)
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> update(@PathVariable ID id, @RequestBody Object request) {
        Object response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " updated successfully")
                .result(response)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable ID id) {
        Object response = service.getById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " retrieved successfully")
                .result(response)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getByUuid(@PathVariable String uuid) {
        Object response = service.getByUuid(uuid);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " retrieved successfully")
                .result(response)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<?> responses = service.getAll();
        return ResponseEntity.ok(ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(entityName + "s retrieved successfully")
                .result(responses)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Page<?>>> getAll(Pageable pageable) {
        Page<?> responses = service.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.<Page<?>>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + "s retrieved successfully")
                .result(responses)
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " deleted successfully")
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteByUuid(@PathVariable String uuid) {
        service.deleteByUuid(uuid);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " deleted successfully")
                .build());
    }

    @Override
    public ResponseEntity<ApiResponse<Page<?>>> search(
            @RequestParam(required = false) BaseSearchRequest searchRequest,
            Pageable pageable) {
        Page<?> responses = service.search(searchRequest, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<?>>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + "s retrieved successfully")
                .result(responses)
                .build());
    }

    @Override
    public ResponseEntity<Resource> importData(@RequestParam("file") MultipartFile file) {
        ByteArrayOutputStream outputStream = service.importData(file);
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_result.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(outputStream.size())
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> exportData(
            @RequestParam(required = false) BaseSearchRequest searchTerm,
            Pageable pageable) {
        ByteArrayOutputStream outputStream = service.exportData(searchTerm, pageable);
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(outputStream.size())
                .body(resource);
    }
} 