package com.woodenfurniture.service;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import com.woodenfurniture.dto.mapper.UserMapper;
import com.woodenfurniture.dto.request.BaseSearchRequest;
import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.exception.UserNotFoundException;
import com.woodenfurniture.repository.RoleRepository;
import com.woodenfurniture.repository.UserRepository;
import com.woodenfurniture.service.impl.BaseServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService extends BaseServiceImpl<User, Long> {
    
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository,
                      RoleRepository roleRepository,
                      UserMapper userMapper,
                      PasswordEncoder passwordEncoder,
                      ExcelService excelService,
                      SimpleExcelConfigReader excelConfigReader) {
        super(userRepository, User.class, excelService, userMapper, excelConfigReader);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getById(String userId) {
        return userMapper.toResponse(userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        log.info("Get info of username: {}", name);

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toResponse(userRepository.save(user));
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse).toList();
    }
    
    @Override
    protected String getImportConfigPath() {
        return "config/excel/user-import-config.json";
    }
    
    @Override
    protected String getExportConfigPath() {
        return "config/excel/user-export-config.json";
    }

    @Override
    @SneakyThrows
    protected Map<User, String> validateEntities(List<User> entities, SimpleExcelConfig config) {
        // Use the base implementation for common validation
        Map<User, String> validationErrors = super.validateEntities(entities, config);
        
        // Add any user-specific validation here if needed
        return validationErrors;
    }
}
