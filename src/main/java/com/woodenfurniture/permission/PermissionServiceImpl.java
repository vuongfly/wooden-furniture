package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseServiceImpl;
import com.woodenfurniture.base.ExcelService;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionServiceImpl extends BaseServiceImpl<Permission, Long, PermissionRequest, PermissionResponse> implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    ExcelService excelService;
    SimpleExcelConfigReader excelConfigReader;

    public PermissionServiceImpl(
            PermissionRepository permissionRepository,
            PermissionMapper permissionMapper,
            ExcelService excelService,
            SimpleExcelConfigReader excelConfigReader) {
        super(permissionRepository, Permission.class, excelService, permissionMapper, excelConfigReader);
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.excelService = excelService;
        this.excelConfigReader = excelConfigReader;
    }

    @Override
    protected String getImportConfigPath() {
        return "config/excel/permission/permission-import-config.json";
    }

    @Override
    protected String getExportConfigPath() {
        return "config/excel/permission-export-config.json";
    }

    @Override
    protected String validateEntity(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }
        return null;
    }

    @Override
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
} 