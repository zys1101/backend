package com.contact.controller;

import com.contact.common.result.Result;
import com.contact.dto.LoginDTO;
import com.contact.dto.RefreshTokenDTO;
import com.contact.service.UserInfoService;
import com.contact.vo.CurrentUserVO;
import com.contact.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 *
 * @author Contact Manager
 */
@Tag(name = "用户认证", description = "登录、登出、刷新Token等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserInfoService userInfoService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "通过用户名和密码登录，返回JWT Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userInfoService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        userInfoService.logout();
        return Result.success("登出成功", null);
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token", description = "使用Refresh Token刷新Access Token")
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginVO loginVO = userInfoService.refreshToken(refreshTokenDTO);
        return Result.success("Token刷新成功", loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/current")
    public Result<CurrentUserVO> getCurrentUser() {
        CurrentUserVO currentUserVO = userInfoService.getCurrentUser();
        return Result.success(currentUserVO);
    }
}
