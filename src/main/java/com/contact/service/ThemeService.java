package com.contact.service;

import com.contact.dto.ThemeUpdateDTO;

/**
 * 用户主题服务接口
 */
public interface ThemeService {

    /**
     * 获取当前用户主题
     */
    String getUserTheme();

    /**
     * 更新当前用户主题
     */
    void updateUserTheme(ThemeUpdateDTO updateDTO);
}
