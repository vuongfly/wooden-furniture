package com.woodenfurniture.province;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ProvinceBaseController {

    @GetMapping
    ResponseEntity<List<ProvinceResponse>> findAll();

    @GetMapping("/{id}")
    ResponseEntity<ProvinceResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<ProvinceResponse> create(@RequestBody ProvinceRequest request);

    @PutMapping("/{id}")
    ResponseEntity<ProvinceResponse> update(@PathVariable Long id, @RequestBody ProvinceRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/code/{provinceCode}")
    ResponseEntity<ProvinceResponse> findByProvinceCode(@PathVariable String provinceCode);

    @GetMapping("/name/{provinceName}")
    ResponseEntity<ProvinceResponse> findByProvinceName(@PathVariable String provinceName);
    
    @GetMapping("/search")
    ResponseEntity<List<ProvinceResponse>> searchProvinces(@RequestParam String provinceParam);
}
