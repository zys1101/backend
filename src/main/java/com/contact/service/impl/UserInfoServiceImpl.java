package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contact.common.constant.ErrorCode;
import com.contact.common.exception.BusinessException;
import com.contact.common.utils.JwtUtil;
import com.contact.common.utils.UserContext;
import com.contact.dto.LoginDTO;
import com.contact.dto.RefreshTokenDTO;
import com.contact.entity.UserTheme;
import com.contact.entity.UserInfo;
import com.contact.mapper.UserThemeMapper;
import com.contact.mapper.UserInfoMapper;
import com.contact.service.UserInfoService;
import com.contact.vo.CurrentUserVO;
import com.contact.vo.LoginVO;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author Contact Manager
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoMapper userInfoMapper;
    private final UserThemeMapper userThemeMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getUsername());

        // 查询用户
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUsername, loginDTO.getUsername());
        UserInfo user = userInfoMapper.selectOne(queryWrapper);

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getUserPassword())) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "密码错误");
        }

        // 验证用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_ERROR, "账号已被禁用");
        }

        // 更新最后登录时间
        UserInfo updateUser = new UserInfo();
        updateUser.setUserId(user.getUserId());
        updateUser.setLastLoginAt(LocalDateTime.now());
        userInfoMapper.updateById(updateUser);

        // 生成Token
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(jwtUtil.getExpiration());

        LoginVO.UserInfoVO userInfoVO = new LoginVO.UserInfoVO();
        userInfoVO.setUserId(user.getUserId());
        userInfoVO.setUsername(user.getUsername());
        loginVO.setUser(userInfoVO);

        log.info("用户登录成功: userId={}", user.getUserId());
        return loginVO;
    }

    @Override
    public void logout() {
        String userId = UserContext.getUserId();
        log.info("用户登出: userId={}", userId);
        // 清除用户上下文
        UserContext.clear();
    }

    @Override
    public LoginVO refreshToken(RefreshTokenDTO refreshTokenDTO) {
        log.info("刷新Token");

        // 解析Refresh Token
        Claims claims = jwtUtil.parseToken(refreshTokenDTO.getRefreshToken());
        if (claims == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "无效的Refresh Token");
        }

        // 验证Token类型
        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token类型错误");
        }

        String userId = claims.get("userId", String.class);
        String username = claims.get("username", String.class);

        // 查询用户
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }

        // 验证用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_ERROR, "账号已被禁用");
        }

        // 生成新的Access Token
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(jwtUtil.getExpiration());

        LoginVO.UserInfoVO userInfoVO = new LoginVO.UserInfoVO();
        userInfoVO.setUserId(user.getUserId());
        userInfoVO.setUsername(user.getUsername());
        loginVO.setUser(userInfoVO);

        log.info("Token刷新成功: userId={}", user.getUserId());
        return loginVO;
    }

    @Override
    public CurrentUserVO getCurrentUser() {
        String userId = UserContext.getUserId();
        log.info("获取当前用户信息: userId={}", userId);

        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }

        CurrentUserVO currentUserVO = new CurrentUserVO();
        currentUserVO.setUserId(user.getUserId());
        currentUserVO.setUsername(user.getUsername());
        currentUserVO.setCreatedAt(user.getCreatedAt());
        currentUserVO.setLastLoginAt(user.getLastLoginAt());

        // 获取主题偏好
        UserTheme userTheme = userThemeMapper.selectById(userId);
        currentUserVO.setTheme(userTheme != null ? userTheme.getTheme() : "light");

        return currentUserVO;
    }
}
