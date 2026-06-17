package com.contact.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 更新联系人DTO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "更新联系人请求")
public class ContactUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "张三")
    @Size(min = 2, max = 20, message = "姓名长度为2-20个字符")
    private String ctName;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138001")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String ctPhone;

    /**
     * 地址
     */
    @Schema(description = "地址", example = "北京市朝阳区")
    @Size(max = 100, message = "地址长度不能超过100个字符")
    private String ctAd;

    /**
     * 邮编
     */
    @Schema(description = "邮编", example = "100000")
    @Pattern(regexp = "^\\d{6}$", message = "邮编必须为6位数字")
    private String ctYb;

    /**
     * QQ号
     */
    @Schema(description = "QQ号", example = "123456789")
    @Pattern(regexp = "^\\d{5,11}$", message = "QQ号格式不正确")
    private String ctQq;

    /**
     * 微信号
     */
    @Schema(description = "微信号", example = "zhangsan123")
    @Size(min = 6, max = 20, message = "微信号长度为6-20个字符")
    private String ctWx;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String ctEm;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "男", allowableValues = {"男", "女"})
    @Pattern(regexp = "^[男女]$", message = "性别只能为男或女")
    private String ctMf;

    /**
     * 出生日期
     */
    @Schema(description = "出生日期", example = "1990-01-01")
    private LocalDate ctBirth;
}
