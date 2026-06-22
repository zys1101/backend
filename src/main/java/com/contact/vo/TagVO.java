package com.contact.vo;

import lombok.Data;

import java.util.List;

/**
 * 联系人标签VO
 */
@Data
public class TagVO {
    
    /** 标签ID */
    private String tagId;
    
    /** 标签名称 */
    private String tagName;
    
    /** 标签颜色 */
    private String tagColor;
    
    /** 联系人ID列表 */
    private List<String> contactIds;
    
    /** 关联联系人数量 */
    private int contactCount;
}
