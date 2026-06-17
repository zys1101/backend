package com.contact.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 事项VO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "事项信息")
public class MatterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事项ID
     */
    @Schema(description = "事项ID")
    private String matterId;

    /**
     * 联系人ID
     */
    @Schema(description = "联系人ID")
    private String ctId;

    /**
     * 联系人姓名
     */
    @Schema(description = "联系人姓名")
    private String contactName;

    /**
     * 事项时间
     */
    @Schema(description = "事项时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matterTime;

    /**
     * 事项内容
     */
    @Schema(description = "事项内容")
    private String matter;

    /**
     * 状态
     */
    @Schema(description = "状态：0待完成 1已取消 2已完成")
    private Integer matterDelete;

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
}
