package com.woodenfurniture.province;

import java.util.List;
import java.util.Optional;

public interface ProvinceService {
    List<ProvinceResponse> findAll();
    ProvinceResponse findById(Long id);
    ProvinceResponse create(ProvinceRequest request);
    ProvinceResponse update(Long id, ProvinceRequest request);
    void delete(Long id);
    Optional<ProvinceResponse> findByProvinceCode(String provinceCode);
    Optional<ProvinceResponse> findByProvinceName(String provinceName);
    List<ProvinceResponse> searchProvinces(String provinceParam);
}
