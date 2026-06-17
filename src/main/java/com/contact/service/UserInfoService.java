package com.contact.service;

import com.contact.dto.LoginDTO;
import com.contact.dto.RefreshTokenDTO;
import com.contact.vo.CurrentUserVO;
import com.contact.vo.LoginVO;

/**
 * 用户服务接口
 *
 * @author Contact Manager
 */
public interface UserInfoService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新Token
     *
     * @param refreshTokenDTO 刷新Token请求
     * @return 登录响应
     */
    LoginVO refreshToken(RefreshTokenDTO refreshTokenDTO);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    CurrentUserVO getCurrentUser();
}
