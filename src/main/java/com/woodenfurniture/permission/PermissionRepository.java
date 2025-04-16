package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
    boolean existsByName(String name);
} 