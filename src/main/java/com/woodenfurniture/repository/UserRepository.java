package com.woodenfurniture.repository;

import com.woodenfurniture.base.BaseRepository;
import com.woodenfurniture.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}
