package com.woodenfurniture.config.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reader for SimpleExcelConfig from JSON files
 */
@Slf4j
@Component
public class SimpleExcelConfigReader {

    private final ObjectMapper objectMapper;

    @Autowired
    public SimpleExcelConfigReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Read configuration from a JSON file in the classpath
     *
     * @param configPath Path to the configuration file (relative to classpath)
     * @return SimpleExcelConfig object
     */
    public SimpleExcelConfig readConfig(String configPath) {
        try {
            ClassPathResource resource = new ClassPathResource(configPath);
            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(inputStream, SimpleExcelConfig.class);
            }
        } catch (IOException e) {
            log.error("Error reading Excel configuration from {}", configPath, e);
            throw new RuntimeException("Failed to read Excel configuration", e);
        }
    }
} 