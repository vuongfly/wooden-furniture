package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseServiceImpl;
import com.woodenfurniture.base.ExcelService;
import com.woodenfurniture.config.excel.SimpleExcelConfig;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.exception.ResourceNotFoundException;
import com.woodenfurniture.exception.UserNotFoundException;
import com.woodenfurniture.role.Role;
import com.woodenfurniture.role.RoleMapper;
import com.woodenfurniture.role.RoleRepository;
import com.woodenfurniture.role.RoleResponse;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserRequest, UserResponse> implements UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    RoleMapper roleMapper;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           ExcelService excelService,
                           SimpleExcelConfigReader excelConfigReader,
                           RoleMapper roleMapper) {
        super(userRepository, User.class, excelService, userMapper, excelConfigReader);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleMapper = roleMapper;
    }

    @Override
    protected String getImportConfigPath() {
        return "config/user-import-config.json";
    }

    @Override
    protected String getExportConfigPath() {
        return "config/user-export-config.json";
    }

    @Override
    public UserResponse getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));
        return mapper.toDto(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Set<RoleResponse> getRolesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));
        
        return user.getRoles().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Create user entity without roles
        User user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new HashSet<>()); // Initialize with empty roles set
        
        // Save user
        user = userRepository.save(user);

        // Create and return response
        return mapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        // Find existing user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));

        // Check if new username or email already exists
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Update user basic fields
        mapper.updateEntityFromDto(request, user);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Save user - don't modify roles here
        user = userRepository.save(user);

        // Create and return response
        return mapper.toDto(user);
    }
    
    @Override
    @Transactional
    public UserResponse addRolesToUser(Long userId, List<String> roleNames) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Initialize roles set if null
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        
        // Add roles
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));
            user.getRoles().add(role);
        }
        
        // Save user
        user = userRepository.save(user);
        
        // Return response
        return mapper.toDto(user);
    }
    
    @Override
    @Transactional
    public UserResponse removeRolesFromUser(Long userId, List<String> roleNames) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // If user has no roles or null roles, return as is
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return mapper.toDto(user);
        }
        
        // Remove roles
        Set<Role> rolesToRemove = new HashSet<>();
        for (String roleName : roleNames) {
            roleRepository.findByName(roleName).ifPresent(rolesToRemove::add);
        }
        
        // Remove all found roles
        user.getRoles().removeAll(rolesToRemove);
        
        // Save user
        user = userRepository.save(user);
        
        // Return response
        return mapper.toDto(user);
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