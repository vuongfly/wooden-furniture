package com.woodenfurniture.c360mappingconfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface C360MappingConfigRepository extends JpaRepository<C360MappingConfig, Long> {
    Optional<C360MappingConfig> findByPermission(String permission);
    
    @Query("SELECT c FROM C360MappingConfig c WHERE " +
           "LOWER(c.permission) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.config) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<C360MappingConfig> searchConfigs(@Param("searchTerm") String searchTerm);
}
