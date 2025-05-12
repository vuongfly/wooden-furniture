package com.woodenfurniture.province;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/provinces")
public interface ProvinceController extends ProvinceBaseController {
}
