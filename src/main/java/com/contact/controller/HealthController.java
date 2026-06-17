package com.contact.controller;

import com.contact.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author Contact Manager
 */
@Tag(name = "系统管理", description = "系统健康检查等接口")
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 健康检查
     */
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("application", "Contact Manager System");
        data.put("version", "1.0.0");
        return Result.success(data);
    }
}
