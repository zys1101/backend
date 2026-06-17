package com.contact.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 联系人头像实体类
 *
 * @author Contact Manager
 */
@Data
@TableName("contact_pic")
public class ContactPic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    @TableId(type = IdType.INPUT)
    private String picId;

    /**
     * 联系人ID
     */
    private String ctId;

    /**
     * 图片文件名
     */
    private String picName;

    /**
     * 图片存储路径
     */
    private String picPath;

    /**
     * 图片大小（字节）
     */
    private Integer picSize;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
