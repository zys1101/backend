package com.contact.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签实体类
 */
@Data
@TableName("contact_tag")
public class ContactTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @TableId(type = IdType.INPUT)
    private String tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 标签颜色
     */
    private String tagColor;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
