package com.woodenfurniture.c360mappingconfig;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/c360-mapping-configs")
public interface C360MappingConfigController extends C360MappingConfigBaseController {
}
