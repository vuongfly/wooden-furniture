package com.woodenfurniture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.woodenfurniture.repository")
@EnableTransactionManagement
public class JpaAuditingConfig {
    // This configuration enables JPA auditing for automatically handling audit fields
    // like createdDate, lastModifiedDate, createdBy, lastModifiedBy
} 