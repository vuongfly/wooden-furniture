package com.woodenfurniture.base;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Base controller interface for common CRUD operations
 *
 * @param <T>   Entity type
 * @param <ID>  ID type
 * @param <Req> Request type
 * @param <Res> Response type
 */
public interface Controller<T extends BaseEntity, ID, Req extends BaseRequest<T>, Res extends BaseResponse<T>> {

    @PostMapping
    ResponseEntity<ApiResponse<Res>> create(@RequestBody Req request);

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<Res>> update(@PathVariable ID id, @RequestBody Req request);

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<Res>> getById(@PathVariable ID id);

    @GetMapping("/uuid/{uuid}")
    ResponseEntity<ApiResponse<Res>> getByUuid(@PathVariable String uuid);

    @GetMapping
    ResponseEntity<ApiResponse<List<Res>>> getAll();

    @GetMapping("/page")
    ResponseEntity<ApiResponse<Page<Res>>> getAll(Pageable pageable);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable ID id);

    @DeleteMapping("/uuid/{uuid}")
    ResponseEntity<ApiResponse<Void>> deleteByUuid(@PathVariable String uuid);

    @GetMapping("/search")
    ResponseEntity<ApiResponse<Page<Res>>> search(
            @RequestParam(required = false) BaseSearchRequest searchRequest,
            Pageable pageable);

    @PostMapping("/import")
    ResponseEntity<Resource> importData(@RequestParam("file") MultipartFile file);

    @GetMapping("/export")
    ResponseEntity<Resource> exportData(
            @RequestParam(required = false) BaseSearchRequest searchTerm,
            Pageable pageable);

    /**
     * Download an Excel import template for this entity.
     */
    @GetMapping("/template")
    ResponseEntity<Resource> generateTemplate();
} 