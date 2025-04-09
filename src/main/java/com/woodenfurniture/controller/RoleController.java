package com.woodenfurniture.controller;

import com.woodenfurniture.dto.request.RoleRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.dto.response.RoleResponse;
import com.woodenfurniture.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {

    RoleService service;

    @PostMapping
    public ApiResponse<RoleResponse> createPermission(@RequestBody @Valid RoleRequest request) {
        log.info("Controller: create Permission");
        return ApiResponse.<RoleResponse>builder()
                .result(service.create(request))
                .build();
    }

    @PutMapping
    public ApiResponse<RoleResponse> update(@RequestBody @Valid RoleRequest request) {
        log.info("Controller: create Permission");
        return ApiResponse.<RoleResponse>builder()
                .result(service.update(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllPermissions() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(service.getAll())
                .build();
    }

    @GetMapping("/{roleName}")
    public ApiResponse<RoleResponse> getAllPermissions(@PathVariable String roleName) {
        return ApiResponse.<RoleResponse>builder()
                .result(service.getByName(roleName))
                .build();
    }

    @DeleteMapping("/{roleId}")
    public ApiResponse<Void> deletePermission(@PathVariable String roleId) {
        service.deleteRole(roleId);
        return ApiResponse.<Void>builder()
                .build();
    }

}
