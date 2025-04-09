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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class BaseController<T extends BaseEntity, ID, DTO> {

    protected final BaseService<T, ID, DTO> service;
    protected final String entityName;

    @PostMapping
    public ResponseEntity<ApiResponse<DTO>> create(@RequestBody DTO dto) {
        DTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DTO>builder()
                        .code(HttpStatus.CREATED.value())
                        .message(entityName + " created successfully")
                        .result(created)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DTO>> update(@PathVariable ID id, @RequestBody DTO dto) {
        DTO updated = service.update(id, dto);
        return ResponseEntity.ok(ApiResponse.<DTO>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " updated successfully")
                .result(updated)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DTO>> getById(@PathVariable ID id) {
        DTO dto = service.getById(id);
        return ResponseEntity.ok(ApiResponse.<DTO>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " retrieved successfully")
                .result(dto)
                .build());
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<ApiResponse<DTO>> getByUuid(@PathVariable String uuid) {
        DTO dto = service.getByUuid(uuid);
        return ResponseEntity.ok(ApiResponse.<DTO>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " retrieved successfully")
                .result(dto)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DTO>>> getAll() {
        List<DTO> dtos = service.getAll();
        return ResponseEntity.ok(ApiResponse.<List<DTO>>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + "s retrieved successfully")
                .result(dtos)
                .build());
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<DTO>>> getAll(Pageable pageable) {
        Page<DTO> dtos = service.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.<Page<DTO>>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + "s retrieved successfully")
                .result(dtos)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " deleted successfully")
                .build());
    }

    @DeleteMapping("/uuid/{uuid}")
    public ResponseEntity<ApiResponse<Void>> deleteByUuid(@PathVariable String uuid) {
        service.deleteByUuid(uuid);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + " deleted successfully")
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<DTO>>> search(
            @RequestParam(required = false) BaseSearchRequest searchRequest,
            Pageable pageable) {
        Page<DTO> dtos = service.search(searchRequest, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<DTO>>builder()
                .code(HttpStatus.OK.value())
                .message(entityName + "s retrieved successfully")
                .result(dtos)
                .build());
    }

    @PostMapping("/import")
    public ResponseEntity<Resource> importData(@RequestParam("file") MultipartFile file) {
        ByteArrayOutputStream outputStream = service.importData(file);
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_result.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(outputStream.size())
                .body(resource);
    }

    @GetMapping("/export")
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