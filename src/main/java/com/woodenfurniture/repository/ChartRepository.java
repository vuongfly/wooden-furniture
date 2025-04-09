package com.woodenfurniture.repository;

import com.woodenfurniture.entity.Chart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
    Optional<Chart> findByCode(String code);
    boolean existsByCode(String code);
    
    @Query("SELECT c FROM Chart c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:code IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :code, '%'))) AND " +
           "(:description IS NULL OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:isShowTitle IS NULL OR c.isShowTitle = :isShowTitle) AND " +
           "(:isShowLegend IS NULL OR c.isShowLegend = :isShowLegend) AND " +
           "(:typeId IS NULL OR c.typeId = :typeId)")
    Page<Chart> searchCharts(
            @Param("name") String name,
            @Param("code") String code,
            @Param("description") String description,
            @Param("isShowTitle") Boolean isShowTitle,
            @Param("isShowLegend") Boolean isShowLegend,
            @Param("typeId") Long typeId,
            Pageable pageable);
} 