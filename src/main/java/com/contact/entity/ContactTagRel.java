package com.contact.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 联系人-标签关联实体类
 */
@Data
@TableName("contact_tag_rel")
public class ContactTagRel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人ID
     */
    private String ctId;

    /**
     * 标签ID
     */
    private String tagId;
}
