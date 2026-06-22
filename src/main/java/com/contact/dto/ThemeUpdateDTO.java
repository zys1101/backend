package com.contact.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户主题更新DTO
 */
@Data
public class ThemeUpdateDTO {
    
    @NotBlank(message = "主题不能为空")
    private String theme; // "light" 或 "dark"
}
