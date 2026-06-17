package com.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 刷新Token DTO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Refresh Token
     */
    @Schema(description = "Refresh Token")
    @NotBlank(message = "Refresh Token不能为空")
    private String refreshToken;
}
