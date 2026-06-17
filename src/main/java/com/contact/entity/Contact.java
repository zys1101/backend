package com.contact.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 联系人实体类
 *
 * @author Contact Manager
 */
@Data
@TableName("contact")
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人ID，格式：C000000001
     */
    @TableId(type = IdType.INPUT)
    private String ctId;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 姓名
     */
    private String ctName;

    /**
     * 地址
     */
    private String ctAd;

    /**
     * 邮编
     */
    private String ctYb;

    /**
     * QQ号
     */
    private String ctQq;

    /**
     * 微信号
     */
    private String ctWx;

    /**
     * 邮箱
     */
    private String ctEm;

    /**
     * 性别：男/女
     */
    private String ctMf;

    /**
     * 出生日期
     */
    private LocalDate ctBirth;

    /**
     * 手机号
     */
    private String ctPhone;

    /**
     * 状态：0正常 1黑名单
     */
    private Integer ctDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
