package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends BaseRepository<Role, Long> {
    boolean existsByName(String name);
    Optional<Role> findByName(String name);
} 