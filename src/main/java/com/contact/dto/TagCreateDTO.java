package com.contact.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建标签DTO
 */
@Data
public class TagCreateDTO {
    
    @NotBlank(message = "标签名称不能为空")
    @Size(min = 1, max = 30, message = "标签名称长度为1-30个字符")
    private String tagName;
    
    private String tagColor;
}
