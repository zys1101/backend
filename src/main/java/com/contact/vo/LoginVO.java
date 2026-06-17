package com.contact.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "登录响应")
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Access Token
     */
    @Schema(description = "Access Token")
    private String token;

    /**
     * Refresh Token
     */
    @Schema(description = "Refresh Token")
    private String refreshToken;

    /**
     * 过期时间（秒）
     */
    @Schema(description = "过期时间（秒）")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfoVO user;

    /**
     * 用户信息VO
     */
    @Data
    @Schema(description = "用户信息")
    public static class UserInfoVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 用户ID
         */
        @Schema(description = "用户ID")
        private String userId;

        /**
         * 用户名
         */
        @Schema(description = "用户名")
        private String username;
    }
}
