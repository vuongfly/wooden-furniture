package com.woodenfurniture.service;

import com.woodenfurniture.dto.request.ChartRequest;
import com.woodenfurniture.dto.request.ChartSearchRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.dto.response.ChartResponse;
import com.woodenfurniture.entity.Chart;
import com.woodenfurniture.exception.ResourceNotFoundException;
import com.woodenfurniture.dto.mapper.ChartMapper;
import com.woodenfurniture.repository.ChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartService {
    private final ChartRepository chartRepository;
    private final ChartMapper chartMapper;

    public List<ChartResponse> getAllCharts() {
        return chartRepository.findAll().stream()
                .map(chartMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ChartResponse getChartById(Long id) {
        Chart chart = chartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chart not found with id: " + id));
        return chartMapper.toResponse(chart);
    }

    public ChartResponse getChartByCode(String code) {
        Chart chart = chartRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Chart not found with code: " + code));
        return chartMapper.toResponse(chart);
    }

    @Transactional
    public ChartResponse createChart(ChartRequest request) {
        if (chartRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Chart with code " + request.getCode() + " already exists");
        }

        Chart chart = chartMapper.toEntity(request);
        Chart savedChart = chartRepository.save(chart);
        return chartMapper.toResponse(savedChart);
    }

    @Transactional
    public ChartResponse updateChart(Long id, ChartRequest request) {
        Chart chart = chartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chart not found with id: " + id));
        chartMapper.updateEntity(chart, request);
        Chart updatedChart = chartRepository.save(chart);
        return chartMapper.toResponse(updatedChart);
    }

    @Transactional
    public void deleteChart(Long id) {
        if (!chartRepository.existsById(id)) {
            throw new ResourceNotFoundException("Chart not found with id: " + id);
        }
        chartRepository.deleteById(id);
    }
    
    public ApiResponse<Page<ChartResponse>> searchCharts(ChartSearchRequest searchRequest, Pageable pageable) {
        // Perform search
        Page<Chart> chartPage = chartRepository.searchCharts(
                searchRequest.getName(),
                searchRequest.getCode(),
                searchRequest.getDescription(),
                searchRequest.getIsShowTitle(),
                searchRequest.getIsShowLegend(),
                searchRequest.getTypeId(),
                pageable
        );
        
        // Convert to ChartResponse
        Page<ChartResponse> responsePage = chartPage.map(chartMapper::toResponse);
        
        return ApiResponse.<Page<ChartResponse>>builder()
                .code(200)
                .message("Charts retrieved successfully")
                .result(responsePage)
                .build();
    }
} 