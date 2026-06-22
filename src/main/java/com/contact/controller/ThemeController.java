package com.contact.controller;

import com.contact.common.result.Result;
import com.contact.dto.ThemeUpdateDTO;
import com.contact.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户主题控制器
 */
@Tag(name = "用户主题", description = "深色/浅色模式切换接口")
@RestController
@RequestMapping("/user/theme")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    /**
     * 获取当前用户主题
     */
    @Operation(summary = "获取当前用户主题", description = "获取当前用户的主题偏好（light/dark）")
    @GetMapping
    public Result<Map<String, String>> getUserTheme() {
        String theme = themeService.getUserTheme();
        Map<String, String> data = new HashMap<>();
        data.put("theme", theme);
        return Result.success(data);
    }

    /**
     * 更新用户主题
     */
    @Operation(summary = "更新用户主题", description = "切换用户的主题偏好（light/dark）")
    @PutMapping
    public Result<Void> updateUserTheme(@Valid @RequestBody ThemeUpdateDTO updateDTO) {
        themeService.updateUserTheme(updateDTO);
        return Result.success("主题已更新", null);
    }
}
