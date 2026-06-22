package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contact.common.utils.UserContext;
import com.contact.dto.ThemeUpdateDTO;
import com.contact.entity.UserTheme;
import com.contact.mapper.UserThemeMapper;
import com.contact.service.ThemeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户主题服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {

    private final UserThemeMapper userThemeMapper;

    @Override
    public String getUserTheme() {
        String userId = UserContext.getUserId();
        UserTheme userTheme = userThemeMapper.selectById(userId);
        return userTheme != null ? userTheme.getTheme() : "light";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserTheme(ThemeUpdateDTO updateDTO) {
        String userId = UserContext.getUserId();
        String theme = updateDTO.getTheme();

        if (!"light".equals(theme) && !"dark".equals(theme)) {
            throw new IllegalArgumentException("不支持的主题: " + theme);
        }

        LambdaQueryWrapper<UserTheme> query = new LambdaQueryWrapper<>();
        query.eq(UserTheme::getUserId, userId);
        UserTheme existing = userThemeMapper.selectOne(query);

        if (existing != null) {
            existing.setTheme(theme);
            existing.setUpdatedAt(LocalDateTime.now());
            userThemeMapper.updateById(existing);
        } else {
            UserTheme userTheme = new UserTheme();
            userTheme.setUserId(userId);
            userTheme.setTheme(theme);
            userTheme.setUpdatedAt(LocalDateTime.now());
            userThemeMapper.insert(userTheme);
        }

        log.info("用户主题已更新: userId={}, theme={}", userId, theme);
    }
}
