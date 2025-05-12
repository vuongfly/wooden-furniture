package com.woodenfurniture.province;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class ProvinceControllerImpl implements ProvinceController {

    private final ProvinceService provinceService;

    @Autowired
    public ProvinceControllerImpl(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @Override
    public ResponseEntity<List<ProvinceResponse>> findAll() {
        return ResponseEntity.ok(provinceService.findAll());
    }

    @Override
    public ResponseEntity<ProvinceResponse> findById(Long id) {
        try {
            return ResponseEntity.ok(provinceService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<ProvinceResponse> create(ProvinceRequest request) {
        ProvinceResponse response = provinceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ProvinceResponse> update(Long id, ProvinceRequest request) {
        try {
            return ResponseEntity.ok(provinceService.update(id, request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        try {
            provinceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<ProvinceResponse> findByProvinceCode(String provinceCode) {
        return provinceService.findByProvinceCode(provinceCode)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ProvinceResponse> findByProvinceName(String provinceName) {
        return provinceService.findByProvinceName(provinceName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<List<ProvinceResponse>> searchProvinces(String provinceParam) {
        List<ProvinceResponse> provinces = provinceService.searchProvinces(provinceParam);
        return ResponseEntity.ok(provinces);
    }
}
