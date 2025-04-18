package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseEntity;
import com.woodenfurniture.enums.Gender;
import com.woodenfurniture.role.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements UserDetails {
    String name;
    @Enumerated(EnumType.STRING)
    Gender gender;
    Integer age;
    String username;
    String password;
    String email;
    String phoneNumber;
    @ManyToMany
    Set<Role> roles;
    LocalDate dob;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return new ArrayList<>();
        }
        
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        
        // Add role-based authorities
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            
            // Add permission-based authorities if the role has permissions
            if (role.getPermissions() != null) {
                authorities.addAll(role.getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                        .collect(Collectors.toSet()));
            }
        }
        
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !getIsDeleted(); // Assuming deleted users should be disabled
    }
}