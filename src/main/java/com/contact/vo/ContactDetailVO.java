package com.contact.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 联系人详情VO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "联系人详情")
public class ContactDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人ID
     */
    @Schema(description = "联系人ID")
    private String ctId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String ctName;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String ctPhone;

    /**
     * 地址
     */
    @Schema(description = "地址")
    private String ctAd;

    /**
     * 邮编
     */
    @Schema(description = "邮编")
    private String ctYb;

    /**
     * QQ号
     */
    @Schema(description = "QQ号")
    private String ctQq;

    /**
     * 微信号
     */
    @Schema(description = "微信号")
    private String ctWx;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String ctEm;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private String ctMf;

    /**
     * 出生日期
     */
    @Schema(description = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ctBirth;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 状态
     */
    @Schema(description = "状态：0正常 1黑名单")
    private Integer ctDelete;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 事项列表
     */
    @Schema(description = "事项列表")
    private List<MatterVO> matters;
}
