package com.woodenfurniture.c360mappingconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class C360MappingConfigServiceImpl implements C360MappingConfigService {

    private final C360MappingConfigRepository c360MappingConfigRepository;
    private final C360MappingConfigMapper c360MappingConfigMapper;

    @Autowired
    public C360MappingConfigServiceImpl(C360MappingConfigRepository c360MappingConfigRepository, 
                                        C360MappingConfigMapper c360MappingConfigMapper) {
        this.c360MappingConfigRepository = c360MappingConfigRepository;
        this.c360MappingConfigMapper = c360MappingConfigMapper;
    }

    @Override
    public List<C360MappingConfigResponse> findAll() {
        return c360MappingConfigRepository.findAll().stream()
                .map(c360MappingConfigMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public C360MappingConfigResponse findById(Long id) {
        return c360MappingConfigRepository.findById(id)
                .map(c360MappingConfigMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("C360MappingConfig not found with id: " + id));
    }

    @Override
    @Transactional
    public C360MappingConfigResponse create(C360MappingConfigRequest request) {
        C360MappingConfig c360MappingConfig = c360MappingConfigMapper.toEntity(request);
        c360MappingConfig = c360MappingConfigRepository.save(c360MappingConfig);
        return c360MappingConfigMapper.toResponse(c360MappingConfig);
    }

    @Override
    @Transactional
    public C360MappingConfigResponse update(Long id, C360MappingConfigRequest request) {
        C360MappingConfig c360MappingConfig = c360MappingConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("C360MappingConfig not found with id: " + id));
        
        c360MappingConfigMapper.updateEntityFromRequest(request, c360MappingConfig);
        c360MappingConfig = c360MappingConfigRepository.save(c360MappingConfig);
        return c360MappingConfigMapper.toResponse(c360MappingConfig);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!c360MappingConfigRepository.existsById(id)) {
            throw new EntityNotFoundException("C360MappingConfig not found with id: " + id);
        }
        c360MappingConfigRepository.deleteById(id);
    }

    @Override
    public Optional<C360MappingConfigResponse> findByPermission(String permission) {
        return c360MappingConfigRepository.findByPermission(permission)
                .map(c360MappingConfigMapper::toResponse);
    }
    
    @Override
    public List<C360MappingConfigResponse> searchConfigs(String searchTerm) {
        return c360MappingConfigRepository.searchConfigs(searchTerm).stream()
                .map(c360MappingConfigMapper::toResponse)
                .collect(Collectors.toList());
    }
}
