package com.woodenfurniture.config;

import com.woodenfurniture.enums.Gender;
import com.woodenfurniture.permission.PermissionRequest;
import com.woodenfurniture.permission.PermissionService;
import com.woodenfurniture.role.RoleRequest;
import com.woodenfurniture.role.RoleService;
import com.woodenfurniture.user.UserRequest;
import com.woodenfurniture.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PermissionService permissionService;
    RoleService roleService;
    UserService userService;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            // Check if sample data already exists
            if (isSampleDataExists()) {
                log.info("Sample data already exists. Skipping initialization.");
                return;
            }

            // Create permissions
            createPermissions();
            
            // Create roles and wait for them to be saved
            createRoles();
            
            // Add a small delay to ensure roles are saved
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Create users
            createUsers();
            
            log.info("Sample data has been created successfully");
        };
    }

    private boolean isSampleDataExists() {
        // Check if any of the sample users exist
        boolean adminExists = userService.existsByUsername("admin");
        boolean managerExists = userService.existsByUsername("manager");
        boolean userExists = userService.existsByUsername("user");

        // Check if any of the sample roles exist
        boolean adminRoleExists = roleService.existsByName("ADMIN");
        boolean managerRoleExists = roleService.existsByName("MANAGER");
        boolean userRoleExists = roleService.existsByName("USER");

        // Check if any of the sample permissions exist
        boolean userReadExists = permissionService.existsByName("Read User");
        boolean roleReadExists = permissionService.existsByName("Read Role");
        boolean permissionReadExists = permissionService.existsByName("Read Permission");

        return adminExists || managerExists || userExists ||
               adminRoleExists || managerRoleExists || userRoleExists ||
               userReadExists || roleReadExists || permissionReadExists;
    }

    private void createPermissions() {
        // User permissions
        createPermission("USER_READ", "Read User", "Permission to view user information");
        createPermission("USER_CREATE", "Create User", "Permission to create new users");
        createPermission("USER_UPDATE", "Update User", "Permission to update user information");
        createPermission("USER_DELETE", "Delete User", "Permission to delete users");

        // Role permissions
        createPermission("ROLE_READ", "Read Role", "Permission to view role information");
        createPermission("ROLE_CREATE", "Create Role", "Permission to create new roles");
        createPermission("ROLE_UPDATE", "Update Role", "Permission to update role information");
        createPermission("ROLE_DELETE", "Delete Role", "Permission to delete roles");

        // Permission permissions
        createPermission("PERMISSION_READ", "Read Permission", "Permission to view permission information");
        createPermission("PERMISSION_CREATE", "Create Permission", "Permission to create new permissions");
        createPermission("PERMISSION_UPDATE", "Update Permission", "Permission to update permission information");
        createPermission("PERMISSION_DELETE", "Delete Permission", "Permission to delete permissions");
    }

    private void createPermission(String code, String name, String description) {
        if (!permissionService.existsByName(name)) {
            PermissionRequest request = new PermissionRequest();
            request.setName(name);
            request.setDescription(description);
            permissionService.create(request);
            log.info("Created permission: {}", name);
        }
    }

    private void createRoles() {
        // Create Admin role with all permissions
        createRole("ADMIN", "ADMIN", "Full system access", Set.of(
                "USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE",
                "ROLE_READ", "ROLE_CREATE", "ROLE_UPDATE", "ROLE_DELETE",
                "PERMISSION_READ", "PERMISSION_CREATE", "PERMISSION_UPDATE", "PERMISSION_DELETE"
        ));

        // Create Manager role with user and role read permissions
        createRole("MANAGER", "MANAGER", "Manager level access", Set.of(
                "USER_READ", "USER_CREATE", "USER_UPDATE",
                "ROLE_READ", "PERMISSION_READ"
        ));

        // Create User role with basic permissions
        createRole("USER", "USER", "Basic user access", Set.of(
                "USER_READ", "ROLE_READ", "PERMISSION_READ"
        ));
    }

    private void createRole(String code, String name, String description, Set<String> permissionNames) {
        if (!roleService.existsByName(name)) {
            RoleRequest request = new RoleRequest();
            request.setName(name);
            request.setDescription(description);
            request.setPermissionNames(permissionNames);
            roleService.create(request);
            log.info("Created role: {}", name);
        }
    }

    private void createUsers() {
        // Create admin user
        createUser("admin", "admin@woodenfurniture.com", "Password123!", "System Administrator", 
                30, Gender.MALE, "+1234567890", LocalDate.of(1993, 1, 1), Set.of("ADMIN"));

        // Create manager user
        createUser("manager", "manager@woodenfurniture.com", "Password123!", "Manager User",
                35, Gender.MALE, "+1234567891", LocalDate.of(1988, 1, 1), Set.of("MANAGER"));

        // Create regular user
        createUser("user", "user@woodenfurniture.com", "Password123!", "Regular User",
                25, Gender.FEMALE, "+1234567892", LocalDate.of(1998, 1, 1), Set.of("USER"));
    }

    private void createUser(String username, String email, String password, String name,
                          Integer age, Gender gender, String phoneNumber, LocalDate dob, Set<String> roles) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setFullName(name);
        request.setAge(age);
        request.setGender(gender);
        request.setPhoneNumber(phoneNumber);
        request.setDob(dob);
        request.setRoles(new ArrayList<>(roles));
        userService.create(request);
        log.info("Created user: {}", username);
    }
}
