package com.woodenfurniture.service;

import com.woodenfurniture.dto.mapper.PermissionMapper;
import com.woodenfurniture.dto.request.PermissionRequest;
import com.woodenfurniture.dto.response.PermissionResponse;
import com.woodenfurniture.entity.Permission;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository repo;
    PermissionMapper mapper;
    PasswordEncoder passwordEncoder;

    public PermissionResponse create(PermissionRequest request) {
        if (repo.existsByName(request.getName()))
            throw new AppException(ErrorCode.PERMISSION_EXISTED);

        Permission permission = mapper.toEntity(request);
        permission = repo.save(permission);
        return mapper.toResponse(permission);
    }

    public List<PermissionResponse> getAllPermissions() {
        return repo.findAll().stream()
                .map(mapper::toResponse).toList();
    }

    public void deletePermission(String permissionId) {
        var permission = repo.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        repo.delete(permission);
    }

}
