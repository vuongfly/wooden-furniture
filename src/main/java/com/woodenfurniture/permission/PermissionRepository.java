package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
    boolean existsByName(String name);
    Set<Permission> findAllByNameIn(Set<String> names);
} 