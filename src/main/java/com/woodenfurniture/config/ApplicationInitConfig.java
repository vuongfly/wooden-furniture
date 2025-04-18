package com.woodenfurniture.config;

import com.woodenfurniture.enums.Gender;
import com.woodenfurniture.permission.PermissionRepository;
import com.woodenfurniture.permission.PermissionRequest;
import com.woodenfurniture.permission.PermissionService;
import com.woodenfurniture.role.RoleRepository;
import com.woodenfurniture.role.RoleRequest;
import com.woodenfurniture.role.RoleService;
import com.woodenfurniture.user.User;
import com.woodenfurniture.user.UserRepository;
import com.woodenfurniture.user.UserRequest;
import com.woodenfurniture.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
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
    UserRepository userRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

//    @Bean
//    public ApplicationRunner applicationRunner() {
//        return new ApplicationRunner() {
//            @Override
//            @Transactional
//            public void run(ApplicationArguments args) throws Exception {
//                log.info("Starting application initialization...");
//
//                // Clear all existing data
//                clearAllData();
//
//                log.info("Creating sample data...");
//
//                // Create permissions
//                createPermissions();
//                log.info("Permissions created successfully");
//
//                // Create roles with permissions
//                createRoles();
//                log.info("Roles created successfully");
//
//                // Create users without roles
//                createUsers();
//                log.info("Users created successfully");
//
//                // Add roles to users
//                assignRolesToUsers();
//                log.info("Roles assigned to users successfully");
//
//                log.info("Sample data has been created successfully");
//            }
//        };
//    }

    @Transactional
    private void clearAllData() {
        log.info("Clearing all existing data...");
        // Delete in correct order to handle foreign key relationships
        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
        log.info("All existing data cleared.");
    }

    private void createPermissions() {
        log.info("Creating permissions...");
        
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
        
        // Product management permissions
        createPermission("PRODUCT_READ", "Read Product", "Permission to view product information");
        createPermission("PRODUCT_CREATE", "Create Product", "Permission to create new products");
        createPermission("PRODUCT_UPDATE", "Update Product", "Permission to update product information");
        createPermission("PRODUCT_DELETE", "Delete Product", "Permission to delete products");
        
        // Order management permissions
        createPermission("ORDER_READ", "Read Order", "Permission to view order information");
        createPermission("ORDER_CREATE", "Create Order", "Permission to create new orders");
        createPermission("ORDER_UPDATE", "Update Order", "Permission to update order information");
        createPermission("ORDER_DELETE", "Delete Order", "Permission to delete orders");
    }

    private void createPermission(String code, String name, String description) {
        PermissionRequest request = new PermissionRequest();
        request.setName(name);
        request.setDescription(description);
        permissionService.create(request);
        log.debug("Created permission: {}", name);
    }

    private void createRoles() {
        log.info("Creating roles...");
        
        // Create Admin role with all permissions
        createRole("ADMIN", "Administrator", "Full system access", Set.of(
                "Read User", "Create User", "Update User", "Delete User",
                "Read Role", "Create Role", "Update Role", "Delete Role",
                "Read Permission", "Create Permission", "Update Permission", "Delete Permission",
                "Read Product", "Create Product", "Update Product", "Delete Product",
                "Read Order", "Create Order", "Update Order", "Delete Order"
        ));

        // Create Manager role with limited permissions
        createRole("MANAGER", "Manager", "Manager level access", Set.of(
                "Read User", "Create User", "Update User",
                "Read Role", "Read Permission",
                "Read Product", "Create Product", "Update Product",
                "Read Order", "Create Order", "Update Order"
        ));

        // Create Sales role
        createRole("SALES", "Sales Representative", "Sales access", Set.of(
                "Read User",
                "Read Product",
                "Read Order", "Create Order", "Update Order"
        ));

        // Create User role with basic permissions
        createRole("USER", "Regular User", "Basic user access", Set.of(
                "Read User",
                "Read Product",
                "Read Order", "Create Order"
        ));
    }

    private void createRole(String code, String name, String description, Set<String> permissionNames) {
        RoleRequest request = new RoleRequest();
        request.setName(name);
        request.setDescription(description);
        request.setPermissionNames(permissionNames);
        roleService.create(request);
        log.debug("Created role: {}", name);
    }

    private void createUsers() {
        log.info("Creating users...");
        
        // Create users without roles
        createUser("admin", "admin@woodenfurniture.com", "Admin123!", "System Administrator", 
                30, Gender.MALE, "+1234567890", LocalDate.of(1993, 1, 1));

        createUser("manager", "manager@woodenfurniture.com", "Manager123!", "Store Manager",
                35, Gender.FEMALE, "+1234567891", LocalDate.of(1988, 1, 1));
                
        createUser("sales", "sales@woodenfurniture.com", "Sales123!", "Sales Person",
                28, Gender.MALE, "+1234567892", LocalDate.of(1995, 5, 15));

        createUser("user", "user@woodenfurniture.com", "User123!", "Regular User",
                25, Gender.FEMALE, "+1234567893", LocalDate.of(1998, 1, 1));
                
        createUser("johndoe", "john.doe@woodenfurniture.com", "John123!", "John Doe",
                40, Gender.MALE, "+1234567894", LocalDate.of(1983, 7, 20));
    }

    private void createUser(String username, String email, String password, String name,
                          Integer age, Gender gender, String phoneNumber, LocalDate dob) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setName(name);
        request.setAge(age);
        request.setGender(gender);
        request.setPhoneNumber(phoneNumber);
        request.setDob(dob);
        userService.create(request);
        log.debug("Created user: {}", username);
    }
    
    private void assignRolesToUsers() {
        log.info("Assigning roles to users...");
        
        // Find users by username
        User adminUser = userRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        
        User managerUser = userRepository.findByUsername("manager")
                .orElseThrow(() -> new RuntimeException("Manager user not found"));
        
        User salesUser = userRepository.findByUsername("sales")
                .orElseThrow(() -> new RuntimeException("Sales user not found"));
        
        User regularUser = userRepository.findByUsername("user")
                .orElseThrow(() -> new RuntimeException("Regular user not found"));
        
        User johnDoe = userRepository.findByUsername("johndoe")
                .orElseThrow(() -> new RuntimeException("John Doe user not found"));
        
        // Assign roles
        userService.addRolesToUser(adminUser.getId(), List.of("Administrator"));
        userService.addRolesToUser(managerUser.getId(), List.of("Manager"));
        userService.addRolesToUser(salesUser.getId(), List.of("Sales Representative"));
        userService.addRolesToUser(regularUser.getId(), List.of("Regular User"));
        userService.addRolesToUser(johnDoe.getId(), List.of("Manager", "Sales Representative"));
        
        log.info("Roles assigned successfully");
    }
}