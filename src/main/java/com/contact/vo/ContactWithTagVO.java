package com.contact.vo;

import com.contact.entity.ContactTag;
import lombok.Data;

import java.util.List;

/**
 * 联系人详情VO（包含标签）
 */
@Data
public class ContactWithTagVO {
    private String ctId;
    private String userId;
    private String ctName;
    private String ctAd;
    private String ctYb;
    private String ctQq;
    private String ctWx;
    private String ctEm;
    private String ctMf;
    private String ctBirth;
    private String ctPhone;
    private Integer ctDelete;
    private String createdAt;
    private String updatedAt;
    private String avatar;
    private List<ContactTag> tags;
}
