package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contact.common.constant.ErrorCode;
import com.contact.common.constant.SystemConstants;
import com.contact.common.exception.BusinessException;
import com.contact.common.utils.IdGenerator;
import com.contact.common.utils.UserContext;
import com.contact.dto.TagAssignDTO;
import com.contact.dto.TagCreateDTO;
import com.contact.entity.Contact;
import com.contact.entity.ContactPic;
import com.contact.entity.ContactTag;
import com.contact.entity.ContactTagRel;
import com.contact.mapper.ContactMapper;
import com.contact.mapper.ContactPicMapper;
import com.contact.mapper.ContactTagMapper;
import com.contact.mapper.ContactTagRelMapper;
import com.contact.service.TagService;
import com.contact.vo.ContactWithTagVO;
import com.contact.vo.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    
    private final ContactTagMapper contactTagMapper;
    private final ContactTagRelMapper contactTagRelMapper;
    private final ContactMapper contactMapper;
    private final ContactPicMapper contactPicMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createTag(TagCreateDTO createDTO) {
        String userId = UserContext.getUserId();
        
        // 检查同名下是否已有标签
        LambdaQueryWrapper<ContactTag> query = new LambdaQueryWrapper<>();
        query.eq(ContactTag::getUserId, userId)
             .eq(ContactTag::getTagName, createDTO.getTagName());
        if (contactTagMapper.selectCount(query) > 0) {
            throw new BusinessException(ErrorCode.RESOURCE_EXISTS, "标签名称已存在");
        }
        
        ContactTag tag = new ContactTag();
        tag.setTagId(IdGenerator.generateTagId());
        tag.setTagName(createDTO.getTagName());
        tag.setUserId(userId);
        tag.setTagColor(createDTO.getTagColor() != null ? createDTO.getTagColor() : "#1890ff");
        tag.setCreatedAt(LocalDateTime.now());
        
        contactTagMapper.insert(tag);
        log.info("创建标签成功: tagId={}, tagName={}", tag.getTagId(), tag.getTagName());
        return tag.getTagId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(String tagId, TagCreateDTO updateDTO) {
        String userId = UserContext.getUserId();
        
        ContactTag tag = contactTagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "标签不存在");
        }
        
        if (!userId.equals(tag.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权修改此标签");
        }
        
        tag.setTagName(updateDTO.getTagName());
        if (updateDTO.getTagColor() != null) {
            tag.setTagColor(updateDTO.getTagColor());
        }
        
        contactTagMapper.updateById(tag);
        log.info("更新标签成功: tagId={}", tagId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(String tagId) {
        String userId = UserContext.getUserId();
        
        ContactTag tag = contactTagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "标签不存在");
        }
        
        if (!userId.equals(tag.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权删除此标签");
        }
        
        // 删除关联关系
        LambdaQueryWrapper<ContactTagRel> relQuery = new LambdaQueryWrapper<>();
        relQuery.eq(ContactTagRel::getTagId, tagId);
        contactTagRelMapper.delete(relQuery);
        
        // 删除标签
        contactTagMapper.deleteById(tagId);
        log.info("删除标签成功: tagId={}", tagId);
    }
    
    @Override
    public List<TagVO> getUserTags() {
        String userId = UserContext.getUserId();
        
        LambdaQueryWrapper<ContactTag> query = new LambdaQueryWrapper<>();
        query.eq(ContactTag::getUserId, userId)
             .orderByAsc(ContactTag::getTagName);
        List<ContactTag> tags = contactTagMapper.selectList(query);
        
        return tags.stream().map(this::toTagVO).collect(Collectors.toList());
    }
    
    @Override
    public List<TagVO> getUserTagsWithStats() {
        String userId = UserContext.getUserId();
        
        LambdaQueryWrapper<ContactTag> query = new LambdaQueryWrapper<>();
        query.eq(ContactTag::getUserId, userId)
             .orderByAsc(ContactTag::getTagName);
        List<ContactTag> tags = contactTagMapper.selectList(query);
        
        // 获取每个标签的联系人数量
        List<TagVO> result = new ArrayList<>();
        for (ContactTag tag : tags) {
            TagVO vo = toTagVO(tag);
            
            // 查询该标签关联的联系人
            LambdaQueryWrapper<ContactTagRel> relQuery = new LambdaQueryWrapper<>();
            relQuery.eq(ContactTagRel::getTagId, tag.getTagId());
            List<ContactTagRel> rels = contactTagRelMapper.selectList(relQuery);
            
            List<String> contactIds = rels.stream().map(ContactTagRel::getCtId).collect(Collectors.toList());
            vo.setContactIds(contactIds);
            vo.setContactCount(contactIds.size());
            
            result.add(vo);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTagsToContact(String ctId, TagAssignDTO assignDTO) {
        String userId = UserContext.getUserId();
        
        // 验证联系人存在
        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }
        
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权操作此联系人");
        }
        
        // 先删除旧关联
        LambdaQueryWrapper<ContactTagRel> deleteQuery = new LambdaQueryWrapper<>();
        deleteQuery.eq(ContactTagRel::getCtId, ctId);
        contactTagRelMapper.delete(deleteQuery);
        
        // 创建新关联
        for (String tagId : assignDTO.getTagIds()) {
            // 验证标签存在
            ContactTag tag = contactTagMapper.selectById(tagId);
            if (tag == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "标签不存在: " + tagId);
            }
            
            if (!userId.equals(tag.getUserId())) {
                throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权使用此标签");
            }
            
            ContactTagRel rel = new ContactTagRel();
            rel.setCtId(ctId);
            rel.setTagId(tagId);
            contactTagRelMapper.insert(rel);
        }
        
        log.info("联系人标签分配成功: ctId={}, tagIds={}", ctId, assignDTO.getTagIds());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagFromContact(String ctId, String tagId) {
        String userId = UserContext.getUserId();
        
        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }
        
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权操作此联系人");
        }
        
        LambdaQueryWrapper<ContactTagRel> query = new LambdaQueryWrapper<>();
        query.eq(ContactTagRel::getCtId, ctId)
             .eq(ContactTagRel::getTagId, tagId);
        contactTagRelMapper.delete(query);
        
        log.info("移除联系人标签成功: ctId={}, tagId={}", ctId, tagId);
    }
    
    @Override
    public List<ContactWithTagVO> getContactsByTagId(String tagId) {
        String userId = UserContext.getUserId();
        
        // 查询该标签关联的联系人ID
        LambdaQueryWrapper<ContactTagRel> relQuery = new LambdaQueryWrapper<>();
        relQuery.eq(ContactTagRel::getTagId, tagId);
        List<ContactTagRel> rels = contactTagRelMapper.selectList(relQuery);
        
        if (rels.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> ctIds = rels.stream().map(ContactTagRel::getCtId).collect(Collectors.toList());
        
        // 查询联系人
        LambdaQueryWrapper<Contact> contactQuery = new LambdaQueryWrapper<>();
        contactQuery.in(Contact::getCtId, ctIds)
                   .eq(Contact::getUserId, userId)
                   .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL);
        List<Contact> contacts = contactMapper.selectList(contactQuery);
        
        // 转换为VO
        return contacts.stream().map(this::toContactWithTagVO).collect(Collectors.toList());
    }
    
    @Override
    public ContactWithTagVO getContactDetailWithTags(String ctId) {
        String userId = UserContext.getUserId();
        
        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }
        
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权访问此联系人");
        }
        
        ContactWithTagVO vo = toContactWithTagVO(contact);
        
        // 查询标签
        LambdaQueryWrapper<ContactTagRel> relQuery = new LambdaQueryWrapper<>();
        relQuery.eq(ContactTagRel::getCtId, ctId);
        List<ContactTagRel> rels = contactTagRelMapper.selectList(relQuery);
        
        if (!rels.isEmpty()) {
            List<String> tagIds = rels.stream().map(ContactTagRel::getTagId).collect(Collectors.toList());
            LambdaQueryWrapper<ContactTag> tagQuery = new LambdaQueryWrapper<>();
            tagQuery.in(ContactTag::getTagId, tagIds);
            List<ContactTag> tags = contactTagMapper.selectList(tagQuery);
            vo.setTags(tags);
        }
        
        return vo;
    }
    
    private TagVO toTagVO(ContactTag tag) {
        TagVO vo = new TagVO();
        vo.setTagId(tag.getTagId());
        vo.setTagName(tag.getTagName());
        vo.setTagColor(tag.getTagColor());
        return vo;
    }
    
    private ContactWithTagVO toContactWithTagVO(Contact contact) {
        ContactWithTagVO vo = new ContactWithTagVO();
        vo.setCtId(contact.getCtId());
        vo.setUserId(contact.getUserId());
        vo.setCtName(contact.getCtName());
        vo.setCtAd(contact.getCtAd());
        vo.setCtYb(contact.getCtYb());
        vo.setCtQq(contact.getCtQq());
        vo.setCtWx(contact.getCtWx());
        vo.setCtEm(contact.getCtEm());
        vo.setCtMf(contact.getCtMf());
        vo.setCtBirth(contact.getCtBirth() != null ? contact.getCtBirth().toString() : null);
        vo.setCtPhone(contact.getCtPhone());
        vo.setCtDelete(contact.getCtDelete());
        vo.setCreatedAt(contact.getCreatedAt() != null ? contact.getCreatedAt().toString() : null);
        vo.setUpdatedAt(contact.getUpdatedAt() != null ? contact.getUpdatedAt().toString() : null);
        
        // 获取头像
        LambdaQueryWrapper<ContactPic> picQuery = new LambdaQueryWrapper<>();
        picQuery.eq(ContactPic::getCtId, contact.getCtId())
                .orderByDesc(ContactPic::getCreatedAt)
                .last("LIMIT 1");
        ContactPic contactPic = contactPicMapper.selectOne(picQuery);
        if (contactPic != null) {
            vo.setAvatar("/uploads/avatar/" + contactPic.getPicName());
        }
        
        return vo;
    }
}