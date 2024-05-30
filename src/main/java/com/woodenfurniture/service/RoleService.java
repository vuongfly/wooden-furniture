package com.woodenfurniture.service;

import com.woodenfurniture.dto.mapper.RoleMapper;
import com.woodenfurniture.dto.request.RoleRequest;
import com.woodenfurniture.dto.response.RoleResponse;
import com.woodenfurniture.entity.Role;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.repository.PermissionRepository;
import com.woodenfurniture.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper mapper;

    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROLE_EXISTED);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        Role role = mapper.toEntity(request);
        role.setPermissions(new HashSet<>(permissions));
        return mapper.toDto(roleRepository.save(role));
    }

    public RoleResponse update(RoleRequest request) {
        if (!roleRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        Role role = mapper.toEntity(request);
        role.setPermissions(new HashSet<>(permissions));
        return mapper.toDto(roleRepository.save(role));
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public void deleteRole(String roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roleRepository.delete(role);
    }

}
