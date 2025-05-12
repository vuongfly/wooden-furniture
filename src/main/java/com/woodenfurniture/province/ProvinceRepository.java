package com.woodenfurniture.province;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    Optional<Province> findByProvinceCode(String provinceCode);
    Optional<Province> findByProvinceName(String provinceName);
    
    @Query("SELECT p FROM Province p WHERE " +
           "LOWER(p.provinceCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.provinceName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.provinceCodeNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.provinceKey) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Province> searchProvinces(@Param("searchTerm") String searchTerm);
}
