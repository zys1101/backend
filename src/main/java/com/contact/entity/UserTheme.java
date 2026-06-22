package com.contact.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户主题偏好实体
 */
@Data
@TableName("user_theme")
public class UserTheme implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（主键）
     */
    @TableId
    private String userId;

    /**
     * 主题: light / dark
     */
    private String theme;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
