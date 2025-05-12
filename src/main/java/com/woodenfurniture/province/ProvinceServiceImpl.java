package com.woodenfurniture.province;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;
    private final ProvinceMapper provinceMapper;

    @Autowired
    public ProvinceServiceImpl(ProvinceRepository provinceRepository, ProvinceMapper provinceMapper) {
        this.provinceRepository = provinceRepository;
        this.provinceMapper = provinceMapper;
    }

    @Override
    public List<ProvinceResponse> findAll() {
        return provinceRepository.findAll().stream()
                .map(provinceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProvinceResponse findById(Long id) {
        return provinceRepository.findById(id)
                .map(provinceMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Province not found with id: " + id));
    }

    @Override
    @Transactional
    public ProvinceResponse create(ProvinceRequest request) {
        Province province = provinceMapper.toEntity(request);
        province = provinceRepository.save(province);
        return provinceMapper.toResponse(province);
    }

    @Override
    @Transactional
    public ProvinceResponse update(Long id, ProvinceRequest request) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Province not found with id: " + id));
        
        provinceMapper.updateEntityFromRequest(request, province);
        province = provinceRepository.save(province);
        return provinceMapper.toResponse(province);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!provinceRepository.existsById(id)) {
            throw new EntityNotFoundException("Province not found with id: " + id);
        }
        provinceRepository.deleteById(id);
    }

    @Override
    public Optional<ProvinceResponse> findByProvinceCode(String provinceCode) {
        return provinceRepository.findByProvinceCode(provinceCode)
                .map(provinceMapper::toResponse);
    }

    @Override
    public Optional<ProvinceResponse> findByProvinceName(String provinceName) {
        return provinceRepository.findByProvinceName(provinceName)
                .map(provinceMapper::toResponse);
    }
    
    @Override
    public List<ProvinceResponse> searchProvinces(String provinceParam) {
        return provinceRepository.searchProvinces(provinceParam).stream()
                .map(provinceMapper::toResponse)
                .collect(Collectors.toList());
    }
}
