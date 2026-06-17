package com.contact.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事项实体类
 *
 * @author Contact Manager
 */
@Data
@TableName("matter")
public class Matter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事项ID，格式：M000000001
     */
    @TableId(type = IdType.INPUT)
    private String matterId;

    /**
     * 联系人ID
     */
    private String ctId;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 事项时间
     */
    private LocalDateTime matterTime;

    /**
     * 事项内容
     */
    private String matter;

    /**
     * 状态：0待完成 1已取消 2已完成
     */
    private Integer matterDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
