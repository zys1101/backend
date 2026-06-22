package com.contact.service;

import com.contact.dto.TagAssignDTO;
import com.contact.dto.TagCreateDTO;
import com.contact.vo.ContactWithTagVO;
import com.contact.vo.TagVO;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {
    
    /**
     * 创建标签
     */
    String createTag(TagCreateDTO createDTO);
    
    /**
     * 更新标签
     */
    void updateTag(String tagId, TagCreateDTO updateDTO);
    
    /**
     * 删除标签
     */
    void deleteTag(String tagId);
    
    /**
     * 获取用户所有标签
     */
    List<TagVO> getUserTags();
    
    /**
     * 为联系人分配标签
     */
    void assignTagsToContact(String ctId, TagAssignDTO assignDTO);
    
    /**
     * 移除联系人标签
     */
    void removeTagFromContact(String ctId, String tagId);
    
    /**
     * 根据标签筛选联系人
     */
    List<ContactWithTagVO> getContactsByTagId(String tagId);
    
    /**
     * 获取联系人详情（含标签）
     */
    ContactWithTagVO getContactDetailWithTags(String ctId);
    
    /**
     * 获取用户标签及关联统计
     */
    List<TagVO> getUserTagsWithStats();
}
