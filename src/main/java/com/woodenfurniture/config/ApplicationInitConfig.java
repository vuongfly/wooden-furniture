package com.woodenfurniture.config;

import com.woodenfurniture.enums.Gender;
import com.woodenfurniture.permission.Permission;
import com.woodenfurniture.permission.PermissionMapper;
import com.woodenfurniture.permission.PermissionRepository;
import com.woodenfurniture.permission.PermissionRequest;
import com.woodenfurniture.permission.PermissionService;
import com.woodenfurniture.role.Role;
import com.woodenfurniture.role.RoleRepository;
import com.woodenfurniture.role.RoleService;
import com.woodenfurniture.user.User;
import com.woodenfurniture.user.UserRepository;
import com.woodenfurniture.user.UserRequest;
import com.woodenfurniture.user.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {

    final PermissionService permissionService;
    final RoleService roleService;
    final UserService userService;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final PermissionRepository permissionRepository;
    final PermissionMapper permissionMapper;

    @Value("${app.init.create-sample-data:false}")
    boolean createSampleData;

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Override
            @Transactional
            public void run(ApplicationArguments args) throws Exception {
                if (!createSampleData) {
                    log.info("Sample data creation is disabled. Skipping initialization.");
                    return;
                }
                
                log.info("Starting application initialization with sample data...");
                
                try {
                    // Clear all existing data
                    clearAllData();
                    
                    log.info("Creating sample data...");
    
                    // Create permissions
                    createPermissions();
                    log.info("Permissions created successfully");
    
                    // Create roles with permissions
                    createRoles();
                    log.info("Roles created successfully");
    
                    // Create users without roles
                    createUsers();
                    log.info("Users created successfully");
                    
                    // Add roles to users
                    assignRolesToUsers();
                    log.info("Roles assigned to users successfully");
    
                    log.info("Sample data has been created successfully");
                } catch (Exception e) {
                    log.error("Error initializing sample data", e);
                    throw e;
                }
            }
        };
    }

    // Inject EntityManager directly
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    private void clearAllData() {
        log.info("Clearing all existing data...");
        try {
            // 1. First, delete from the mapping tables (join tables for many-to-many)
            entityManager.createNativeQuery("DELETE FROM user_roles").executeUpdate();
            log.info("Cleared user-role mappings");
            
            entityManager.createNativeQuery("DELETE FROM role_permissions").executeUpdate();
            log.info("Cleared role-permission mappings");
            
            // 2. Now delete from the entity tables in the correct order
            entityManager.createNativeQuery("DELETE FROM user").executeUpdate();
            log.info("Deleted all users");
            
            entityManager.createNativeQuery("DELETE FROM role").executeUpdate();
            log.info("Deleted all roles");
            
            entityManager.createNativeQuery("DELETE FROM permission").executeUpdate();
            log.info("Deleted all permissions");
            
            // Flush to ensure all changes are committed
            entityManager.flush();
            
            log.info("All existing data cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing data", e);
            throw e;
        }
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
        try {
            // Create a permission request with all the data
            PermissionRequest request = new PermissionRequest();
            request.setName(name);
            request.setDescription(description);
            
            // Create the permission first
            Permission permission = permissionMapper.toEntity(request);
            
            // Set the code using the inherited field from BaseEntity
            permission.setCode(code);
            
            // Save directly using repository
            permissionRepository.save(permission);
            log.debug("Created permission: {} with code: {}", name, code);
        } catch (Exception e) {
            log.error("Error creating permission with code: {}", code, e);
            throw e;
        }
    }

    private void createRoles() {
        log.info("Creating roles...");
        
        // Create Admin role with all permissions
        createRole("ADMIN_ROLE", "Administrator", "Full system access", Set.of(
                "USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE",
                "ROLE_READ", "ROLE_CREATE", "ROLE_UPDATE", "ROLE_DELETE",
                "PERMISSION_READ", "PERMISSION_CREATE", "PERMISSION_UPDATE", "PERMISSION_DELETE",
                "PRODUCT_READ", "PRODUCT_CREATE", "PRODUCT_UPDATE", "PRODUCT_DELETE",
                "ORDER_READ", "ORDER_CREATE", "ORDER_UPDATE", "ORDER_DELETE"
        ));

        // Create Manager role with limited permissions
        createRole("MANAGER_ROLE", "Manager", "Manager level access", Set.of(
                "USER_READ", "USER_CREATE", "USER_UPDATE",
                "ROLE_READ", "PERMISSION_READ",
                "PRODUCT_READ", "PRODUCT_CREATE", "PRODUCT_UPDATE",
                "ORDER_READ", "ORDER_CREATE", "ORDER_UPDATE"
        ));

        // Create Sales role
        createRole("SALES_ROLE", "Sales Representative", "Sales access", Set.of(
                "USER_READ",
                "PRODUCT_READ",
                "ORDER_READ", "ORDER_CREATE", "ORDER_UPDATE"
        ));

        // Create User role with basic permissions
        createRole("USER_ROLE", "Regular User", "Basic user access", Set.of(
                "USER_READ",
                "PRODUCT_READ",
                "ORDER_READ", "ORDER_CREATE"
        ));
    }

    private void createRole(String code, String name, String description, Set<String> permissionCodes) {
        try {
            // First create a basic role with name and description
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            
            // Set the code using the inherited field from BaseEntity
            role.setCode(code);
            
            // First find permissions by their code (now that we're properly setting codes)
            Set<Permission> permissions = permissionRepository.findAllByCodeIn(permissionCodes);
            
            if (permissions.isEmpty()) {
                // Fallback to finding by name if no permissions found by code
                Set<String> permissionNames = permissionCodes.stream()
                    .map(pCode -> {
                        switch(pCode) {
                            case "USER_READ": return "Read User";
                            case "USER_CREATE": return "Create User";
                            case "USER_UPDATE": return "Update User";
                            case "USER_DELETE": return "Delete User";
                            case "ROLE_READ": return "Read Role";
                            case "ROLE_CREATE": return "Create Role";
                            case "ROLE_UPDATE": return "Update Role";
                            case "ROLE_DELETE": return "Delete Role";
                            case "PERMISSION_READ": return "Read Permission";
                            case "PERMISSION_CREATE": return "Create Permission";
                            case "PERMISSION_UPDATE": return "Update Permission";
                            case "PERMISSION_DELETE": return "Delete Permission";
                            case "PRODUCT_READ": return "Read Product";
                            case "PRODUCT_CREATE": return "Create Product";
                            case "PRODUCT_UPDATE": return "Update Product";
                            case "PRODUCT_DELETE": return "Delete Product";
                            case "ORDER_READ": return "Read Order";
                            case "ORDER_CREATE": return "Create Order";
                            case "ORDER_UPDATE": return "Update Order";
                            case "ORDER_DELETE": return "Delete Order";
                            default: return pCode;
                        }
                    })
                    .collect(java.util.stream.Collectors.toSet());
                
                permissions = permissionRepository.findAllByNameIn(permissionNames);
            }
            
            if (permissions.size() != permissionCodes.size()) {
                log.warn("Not all permissions were found: expected {}, found {}", 
                        permissionCodes.size(), permissions.size());
            }
            
            // Set the permissions for the role
            role.setPermissions(permissions);
            
            // Save the role with its code and permissions
            roleRepository.save(role);
            
            log.debug("Created role: {} with code: {} and {} permissions", 
                    name, code, permissions.size());
        } catch (Exception e) {
            log.error("Error creating role with code: {}", code, e);
            throw e;
        }
    }

    private void createUsers() {
        log.info("Creating users...");
        
        // Create users without roles
        createUser("admin", "admin@woodenfurniture.com", "admin", "System Administrator",
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
        request.setName(name);  // Using name field as per UserRequest
        request.setAge(age);
        request.setGender(gender);
        request.setPhoneNumber(phoneNumber);
        request.setDob(dob);
        userService.create(request);
        log.debug("Created user: {}", username);
    }
    
    private void assignRolesToUsers() {
        log.info("Assigning roles to users...");
        
        try {
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
            
            log.info("All users found successfully");
            
            // Try to find roles by code first
            Role adminRole = roleRepository.findByCode("ADMIN_ROLE")
                    .orElseGet(() -> roleRepository.findByName("Administrator")
                            .orElseThrow(() -> new RuntimeException("Admin role not found")));
            
            Role managerRole = roleRepository.findByCode("MANAGER_ROLE")
                    .orElseGet(() -> roleRepository.findByName("Manager")
                            .orElseThrow(() -> new RuntimeException("Manager role not found")));
            
            Role salesRole = roleRepository.findByCode("SALES_ROLE")
                    .orElseGet(() -> roleRepository.findByName("Sales Representative")
                            .orElseThrow(() -> new RuntimeException("Sales role not found")));
            
            Role userRole = roleRepository.findByCode("USER_ROLE")
                    .orElseGet(() -> roleRepository.findByName("Regular User")
                            .orElseThrow(() -> new RuntimeException("User role not found")));
            
            log.info("All roles found successfully");
            
            // Initialize roles sets if they're null
            if (adminUser.getRoles() == null) adminUser.setRoles(new java.util.HashSet<>());
            if (managerUser.getRoles() == null) managerUser.setRoles(new java.util.HashSet<>());
            if (salesUser.getRoles() == null) salesUser.setRoles(new java.util.HashSet<>());
            if (regularUser.getRoles() == null) regularUser.setRoles(new java.util.HashSet<>());
            if (johnDoe.getRoles() == null) johnDoe.setRoles(new java.util.HashSet<>());
            
            // Clear existing roles first to avoid duplicates
            adminUser.getRoles().clear();
            managerUser.getRoles().clear();
            salesUser.getRoles().clear();
            regularUser.getRoles().clear();
            johnDoe.getRoles().clear();
            
            // Assign roles directly
            adminUser.getRoles().add(adminRole);
            managerUser.getRoles().add(managerRole);
            salesUser.getRoles().add(salesRole);
            regularUser.getRoles().add(userRole);
            johnDoe.getRoles().add(managerRole);
            johnDoe.getRoles().add(salesRole);
            
            // Save users
            userRepository.save(adminUser);
            userRepository.save(managerUser);
            userRepository.save(salesUser);
            userRepository.save(regularUser);
            userRepository.save(johnDoe);
            
            log.info("Roles assigned successfully");
        } catch (Exception e) {
            log.error("Error assigning roles to users", e);
            throw e;
        }
    }
}