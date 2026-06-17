package com.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 更新事项DTO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "更新事项请求")
public class MatterUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事项时间
     */
    @Schema(description = "事项时间", example = "2026-06-20T10:00:00")
    private LocalDateTime matterTime;

    /**
     * 事项内容
     */
    @Schema(description = "事项内容", example = "开会讨论项目进度")
    @Size(max = 100, message = "事项内容长度不能超过100个字符")
    private String matter;
}
