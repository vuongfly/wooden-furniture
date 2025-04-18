package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseServiceImpl;
import com.woodenfurniture.base.ExcelService;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.exception.ResourceNotFoundException;
import com.woodenfurniture.permission.Permission;
import com.woodenfurniture.permission.PermissionMapper;
import com.woodenfurniture.permission.PermissionRepository;
import com.woodenfurniture.permission.PermissionResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<Role, Long, RoleRequest, RoleResponse> implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    ExcelService excelService;
    SimpleExcelConfigReader excelConfigReader;
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            ExcelService excelService,
            SimpleExcelConfigReader excelConfigReader,
            PermissionRepository permissionRepository,
            PermissionMapper permissionMapper) {
        super(roleRepository, Role.class, excelService, roleMapper, excelConfigReader);
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.excelService = excelService;
        this.excelConfigReader = excelConfigReader;
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    protected String getImportConfigPath() {
        return "config/role-import-config.json";
    }

    @Override
    protected String getExportConfigPath() {
        return "config/excel/role-export-config.json";
    }

    @Override
    protected String validateEntity(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        return null;
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toEntity(request);
        Set<Permission> permissions = permissionRepository.findAllByNameIn(request.getPermissionNames());
        role.setPermissions(permissions);
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public RoleResponse getByName(String name) {
        return roleRepository.findByName(name)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        Role updatedRole = roleMapper.toEntity(request);
        updatedRole.setId(id);
        Set<Permission> permissions = permissionRepository.findAllByNameIn(request.getPermissionNames());
        updatedRole.setPermissions(permissions);

        return roleMapper.toDto(roleRepository.save(updatedRole));
    }
    
    @Override
    public Set<PermissionResponse> getPermissionsByRoleId(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
                
        return role.getPermissions().stream()
                .map(permissionMapper::toDto)
                .collect(Collectors.toSet());
    }
}