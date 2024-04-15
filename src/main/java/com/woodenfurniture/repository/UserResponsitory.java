package com.woodenfurniture.repository;

import com.woodenfurniture.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserResponsitory extends JpaRepository<User, String> {
}
