package com.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 新增事项DTO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "新增事项请求")
public class MatterCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人ID
     */
    @Schema(description = "联系人ID", example = "C000000001")
    @NotBlank(message = "联系人ID不能为空")
    private String ctId;

    /**
     * 事项时间
     */
    @Schema(description = "事项时间", example = "2026-06-20T10:00:00")
    @NotNull(message = "事项时间不能为空")
    private LocalDateTime matterTime;

    /**
     * 事项内容
     */
    @Schema(description = "事项内容", example = "开会讨论项目进度")
    @NotBlank(message = "事项内容不能为空")
    @Size(max = 100, message = "事项内容长度不能超过100个字符")
    private String matter;
}
