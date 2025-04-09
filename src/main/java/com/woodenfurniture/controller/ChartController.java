package com.woodenfurniture.controller;

import com.woodenfurniture.dto.ChartRequest;
import com.woodenfurniture.dto.request.ChartSearchRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.dto.response.ChartResponse;
import com.woodenfurniture.service.ChartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartController {
    private final ChartService chartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChartResponse>>> getAllCharts() {
        List<ChartResponse> charts = chartService.getAllCharts();
        return ResponseEntity.ok(ApiResponse.<List<ChartResponse>>builder()
                .code(200)
                .message("Charts retrieved successfully")
                .result(charts)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChartResponse>> getChartById(@PathVariable Long id) {
        ChartResponse chart = chartService.getChartById(id);
        return ResponseEntity.ok(ApiResponse.<ChartResponse>builder()
                .code(200)
                .message("Chart retrieved successfully")
                .result(chart)
                .build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ChartResponse>> getChartByCode(@PathVariable String code) {
        ChartResponse chart = chartService.getChartByCode(code);
        return ResponseEntity.ok(ApiResponse.<ChartResponse>builder()
                .code(200)
                .message("Chart retrieved successfully")
                .result(chart)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChartResponse>> createChart(@Valid @RequestBody ChartRequest request) {
        ChartResponse chart = chartService.createChart(request);
        return new ResponseEntity<>(ApiResponse.<ChartResponse>builder()
                .code(201)
                .message("Chart created successfully")
                .result(chart)
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChartResponse>> updateChart(@PathVariable Long id, @Valid @RequestBody ChartRequest request) {
        ChartResponse chart = chartService.updateChart(id, request);
        return ResponseEntity.ok(ApiResponse.<ChartResponse>builder()
                .code(200)
                .message("Chart updated successfully")
                .result(chart)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChart(@PathVariable Long id) {
        chartService.deleteChart(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Chart deleted successfully")
                .build());
    }
    
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<ChartResponse>>> searchCharts(
            @RequestBody ChartSearchRequest searchRequest,
            Pageable pageable) {
        return ResponseEntity.ok(chartService.searchCharts(searchRequest, pageable));
    }
} 