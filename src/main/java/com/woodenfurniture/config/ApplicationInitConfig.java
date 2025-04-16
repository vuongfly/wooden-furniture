package com.woodenfurniture.config;

import com.woodenfurniture.role.Role;
import com.woodenfurniture.user.User;
import com.woodenfurniture.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // create default role ADMIN
            Set<Role> roles = new HashSet<>();
            if (userRepository.findByUsername("admin").isEmpty()) {
                var roleAdmin = roleRepository.findById("ADMIN");
                if (roleAdmin.isEmpty()) {
                    Role role = Role.builder()
                            .name("ADMIN")
                            .description("Default Admin role")
                            .build();
                    roleRepository.save(role);
                    roles.add(role);
                } else {
                    roles.add(roleAdmin.get());
                }
                // create default user ADMIN
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin");
            }
        };
    }
}
