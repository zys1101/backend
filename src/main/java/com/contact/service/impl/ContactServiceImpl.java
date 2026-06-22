package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.constant.ErrorCode;
import com.contact.common.constant.SystemConstants;
import com.contact.common.exception.BusinessException;
import com.contact.common.utils.IdGenerator;
import com.contact.common.utils.UserContext;
import com.contact.dto.ContactCreateDTO;
import com.contact.dto.ContactUpdateDTO;
import com.contact.entity.Contact;
import com.contact.entity.ContactPic;
import com.contact.entity.ContactTag;
import com.contact.entity.ContactTagRel;
import com.contact.entity.Matter;
import com.contact.mapper.ContactMapper;
import com.contact.mapper.ContactPicMapper;
import com.contact.mapper.ContactTagMapper;
import com.contact.mapper.ContactTagRelMapper;
import com.contact.mapper.MatterMapper;
import com.contact.service.ContactService;
import com.contact.vo.ContactDetailVO;
import com.contact.vo.ContactVO;
import com.contact.vo.MatterVO;
import com.contact.vo.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 联系人服务实现类
 *
 * @author Contact Manager
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMapper contactMapper;
    private final ContactPicMapper contactPicMapper;
    private final MatterMapper matterMapper;
    private final ContactTagMapper contactTagMapper;
    private final ContactTagRelMapper contactTagRelMapper;

    @Value("${file.upload.avatar-path:uploads/avatar/}")
    private String avatarPath;

    @Value("${file.upload.allowed-types:jpg,jpeg,png}")
    private String allowedTypes;

    @Value("${file.upload.max-size:5242880}")
    private Long maxSize;

    @Override
    public Page<ContactVO> getContactList(Integer page, Integer pageSize, String ctName,
                                           String ctPhone, String ctMf, String sortBy, String sortOrder) {
        String userId = UserContext.getUserId();
        log.info("查询联系人列表: userId={}, page={}, pageSize={}", userId, page, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId)
                    .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL);

        // 姓名模糊搜索
        if (StringUtils.hasText(ctName)) {
            queryWrapper.like(Contact::getCtName, ctName);
        }

        // 电话模糊搜索
        if (StringUtils.hasText(ctPhone)) {
            queryWrapper.like(Contact::getCtPhone, ctPhone);
        }

        // 性别筛选
        if (StringUtils.hasText(ctMf)) {
            queryWrapper.eq(Contact::getCtMf, ctMf);
        }

        // 排序
        if (StringUtils.hasText(sortBy)) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            switch (sortBy) {
                case "ctName":
                    queryWrapper.orderBy(true, isAsc, Contact::getCtName);
                    break;
                case "createdAt":
                    queryWrapper.orderBy(true, isAsc, Contact::getCreatedAt);
                    break;
                default:
                    queryWrapper.orderByDesc(Contact::getCreatedAt);
            }
        } else {
            queryWrapper.orderByDesc(Contact::getCreatedAt);
        }

        // 分页查询
        Page<Contact> contactPage = contactMapper.selectPage(
                new Page<>(page, pageSize), queryWrapper);

        // 转换为VO
        Page<ContactVO> voPage = new Page<>();
        voPage.setCurrent(contactPage.getCurrent());
        voPage.setSize(contactPage.getSize());
        voPage.setTotal(contactPage.getTotal());
        voPage.setPages(contactPage.getPages());

        List<ContactVO> voList = contactPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public ContactDetailVO getContactDetail(String ctId) {
        String userId = UserContext.getUserId();
        log.info("查询联系人详情: userId={}, ctId={}", userId, ctId);

        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }

        // 验证权限
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权访问此联系人");
        }

        ContactDetailVO detailVO = new ContactDetailVO();
        BeanUtils.copyProperties(contact, detailVO);

        // 获取头像
        LambdaQueryWrapper<ContactPic> picQuery = new LambdaQueryWrapper<>();
        picQuery.eq(ContactPic::getCtId, ctId)
                .orderByDesc(ContactPic::getCreatedAt)
                .last("LIMIT 1");
        ContactPic contactPic = contactPicMapper.selectOne(picQuery);
        if (contactPic != null) {
            detailVO.setAvatar("/uploads/avatar/" + contactPic.getPicName());
        }

        // 获取事项列表
        LambdaQueryWrapper<Matter> matterQuery = new LambdaQueryWrapper<>();
        matterQuery.eq(Matter::getCtId, ctId)
                   .orderByDesc(Matter::getMatterTime);
        List<Matter> matters = matterMapper.selectList(matterQuery);
        List<MatterVO> matterVOList = matters.stream()
                .map(this::convertMatterToVO)
                .collect(Collectors.toList());
        detailVO.setMatters(matterVOList);

        // 获取标签列表
        detailVO.setTags(getTagVOsForContact(ctId));

        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createContact(ContactCreateDTO createDTO) {
        String userId = UserContext.getUserId();
        log.info("新增联系人: userId={}, name={}", userId, createDTO.getCtName());

        // 检查手机号是否已存在
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId)
                    .eq(Contact::getCtPhone, createDTO.getCtPhone());
        Long count = contactMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.RESOURCE_EXISTS, "手机号已存在");
        }

        // 创建联系人
        Contact contact = new Contact();
        BeanUtils.copyProperties(createDTO, contact);
        contact.setCtId(IdGenerator.generateContactId());
        contact.setUserId(userId);
        contact.setCtDelete(SystemConstants.CONTACT_STATUS_NORMAL);
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());

        contactMapper.insert(contact);
        log.info("联系人创建成功: ctId={}", contact.getCtId());

        return contact.getCtId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContact(String ctId, ContactUpdateDTO updateDTO) {
        String userId = UserContext.getUserId();
        log.info("更新联系人: userId={}, ctId={}", userId, ctId);

        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }

        // 验证权限
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权修改此联系人");
        }

        // 更新联系人信息
        if (StringUtils.hasText(updateDTO.getCtName())) {
            contact.setCtName(updateDTO.getCtName());
        }
        if (StringUtils.hasText(updateDTO.getCtPhone())) {
            // 检查手机号是否已被其他联系人使用
            LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Contact::getUserId, userId)
                        .eq(Contact::getCtPhone, updateDTO.getCtPhone())
                        .ne(Contact::getCtId, ctId);
            Long count = contactMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.RESOURCE_EXISTS, "手机号已被其他联系人使用");
            }
            contact.setCtPhone(updateDTO.getCtPhone());
        }
        if (updateDTO.getCtAd() != null) {
            contact.setCtAd(updateDTO.getCtAd());
        }
        if (updateDTO.getCtYb() != null) {
            contact.setCtYb(updateDTO.getCtYb());
        }
        if (updateDTO.getCtQq() != null) {
            contact.setCtQq(updateDTO.getCtQq());
        }
        if (updateDTO.getCtWx() != null) {
            contact.setCtWx(updateDTO.getCtWx());
        }
        if (updateDTO.getCtEm() != null) {
            contact.setCtEm(updateDTO.getCtEm());
        }
        if (StringUtils.hasText(updateDTO.getCtMf())) {
            contact.setCtMf(updateDTO.getCtMf());
        }
        if (updateDTO.getCtBirth() != null) {
            contact.setCtBirth(updateDTO.getCtBirth());
        }
        contact.setUpdatedAt(LocalDateTime.now());

        contactMapper.updateById(contact);
        log.info("联系人更新成功: ctId={}", ctId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContact(String ctId) {
        String userId = UserContext.getUserId();
        log.info("删除联系人: userId={}, ctId={}", userId, ctId);

        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }

        // 验证权限
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权删除此联系人");
        }

        // 删除联系人（同时会删除相关的事项和头像，因为有外键级联删除）
        contactMapper.deleteById(ctId);
        log.info("联系人删除成功: ctId={}", ctId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(String ctId, MultipartFile file) {
        String userId = UserContext.getUserId();
        log.info("上传头像: userId={}, ctId={}", userId, ctId);

        // 验证联系人是否存在
        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }

        // 验证权限
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权上传头像");
        }

        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件大小超过限制（最大5MB）");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "";
        List<String> allowedTypesList = Arrays.asList(allowedTypes.split(","));
        if (!allowedTypesList.contains(extension)) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, 
                    "文件类型不支持，仅支持: " + allowedTypes);
        }

        try {
            // 创建上传目录
            Path uploadPath = Paths.get(avatarPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成新文件名
            String newFileName = UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadPath.resolve(newFileName);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 保存到数据库
            ContactPic contactPic = new ContactPic();
            contactPic.setPicId(IdGenerator.generatePictureId());
            contactPic.setCtId(ctId);
            contactPic.setPicName(newFileName);
            contactPic.setPicPath(filePath.toString());
            contactPic.setPicSize((int) file.getSize());
            contactPic.setMimeType(file.getContentType());
            contactPic.setCreatedAt(LocalDateTime.now());
            contactPicMapper.insert(contactPic);

            log.info("头像上传成功: picId={}, fileName={}", contactPic.getPicId(), newFileName);
            return "/uploads/avatar/" + newFileName;

        } catch (IOException e) {
            log.error("头像上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件上传失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToBlacklist(String ctId) {
        String userId = UserContext.getUserId();
        log.info("加入黑名单: userId={}, ctId={}", userId, ctId);

        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }

        // 验证权限
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权操作此联系人");
        }

        // 更新状态
        contact.setCtDelete(SystemConstants.CONTACT_STATUS_BLACKLIST);
        contact.setUpdatedAt(LocalDateTime.now());
        contactMapper.updateById(contact);

        log.info("加入黑名单成功: ctId={}", ctId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreContact(String ctId) {
        String userId = UserContext.getUserId();
        log.info("恢复联系人: userId={}, ctId={}", userId, ctId);

        Contact contact = contactMapper.selectById(ctId);
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }

        // 验证权限
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权操作此联系人");
        }

        // 更新状态
        contact.setCtDelete(SystemConstants.CONTACT_STATUS_NORMAL);
        contact.setUpdatedAt(LocalDateTime.now());
        contactMapper.updateById(contact);

        log.info("恢复联系人成功: ctId={}", ctId);
    }

    @Override
    public Page<ContactVO> getBlacklist(Integer page, Integer pageSize, String ctName,
                                         String ctPhone, String ctMf, String sortBy, String sortOrder) {
        String userId = UserContext.getUserId();
        log.info("查询黑名单列表: userId={}, page={}, pageSize={}", userId, page, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId)
                    .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_BLACKLIST);

        // 姓名模糊搜索
        if (StringUtils.hasText(ctName)) {
            queryWrapper.like(Contact::getCtName, ctName);
        }

        // 电话模糊搜索
        if (StringUtils.hasText(ctPhone)) {
            queryWrapper.like(Contact::getCtPhone, ctPhone);
        }

        // 性别筛选
        if (StringUtils.hasText(ctMf)) {
            queryWrapper.eq(Contact::getCtMf, ctMf);
        }

        // 排序
        if (StringUtils.hasText(sortBy)) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            switch (sortBy) {
                case "ctName":
                    queryWrapper.orderBy(true, isAsc, Contact::getCtName);
                    break;
                case "createdAt":
                    queryWrapper.orderBy(true, isAsc, Contact::getCreatedAt);
                    break;
                default:
                    queryWrapper.orderByDesc(Contact::getCreatedAt);
            }
        } else {
            queryWrapper.orderByDesc(Contact::getCreatedAt);
        }

        // 分页查询
        Page<Contact> contactPage = contactMapper.selectPage(
                new Page<>(page, pageSize), queryWrapper);

        // 转换为VO
        Page<ContactVO> voPage = new Page<>();
        voPage.setCurrent(contactPage.getCurrent());
        voPage.setSize(contactPage.getSize());
        voPage.setTotal(contactPage.getTotal());
        voPage.setPages(contactPage.getPages());

        List<ContactVO> voList = contactPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 转换为VO
     */
    private ContactVO convertToVO(Contact contact) {
        ContactVO vo = new ContactVO();
        BeanUtils.copyProperties(contact, vo);

        // 获取头像
        LambdaQueryWrapper<ContactPic> picQuery = new LambdaQueryWrapper<>();
        picQuery.eq(ContactPic::getCtId, contact.getCtId())
                .orderByDesc(ContactPic::getCreatedAt)
                .last("LIMIT 1");
        ContactPic contactPic = contactPicMapper.selectOne(picQuery);
        if (contactPic != null) {
            vo.setAvatar("/uploads/avatar/" + contactPic.getPicName());
        }

        // 获取标签列表
        vo.setTags(getTagNamesForContact(contact.getCtId()));

        return vo;
    }

    /**
     * 转换事项为VO
     */
    private MatterVO convertMatterToVO(Matter matter) {
        MatterVO vo = new MatterVO();
        BeanUtils.copyProperties(matter, vo);

        // 获取联系人姓名
        Contact contact = contactMapper.selectById(matter.getCtId());
        if (contact != null) {
            vo.setContactName(contact.getCtName());
        }

        return vo;
    }

    /**
     * 获取联系人标签名称列表
     */
    private List<String> getTagNamesForContact(String ctId) {
        LambdaQueryWrapper<ContactTagRel> relQuery = new LambdaQueryWrapper<>();
        relQuery.eq(ContactTagRel::getCtId, ctId);
        List<ContactTagRel> rels = contactTagRelMapper.selectList(relQuery);

        if (rels.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> tagIds = rels.stream().map(ContactTagRel::getTagId).collect(Collectors.toList());
        LambdaQueryWrapper<ContactTag> tagQuery = new LambdaQueryWrapper<>();
        tagQuery.in(ContactTag::getTagId, tagIds);
        List<ContactTag> tags = contactTagMapper.selectList(tagQuery);

        return tags.stream().map(ContactTag::getTagName).collect(Collectors.toList());
    }

    /**
     * 获取联系人标签VO列表
     */
    private List<TagVO> getTagVOsForContact(String ctId) {
        LambdaQueryWrapper<ContactTagRel> relQuery = new LambdaQueryWrapper<>();
        relQuery.eq(ContactTagRel::getCtId, ctId);
        List<ContactTagRel> rels = contactTagRelMapper.selectList(relQuery);

        if (rels.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> tagIds = rels.stream().map(ContactTagRel::getTagId).collect(Collectors.toList());
        LambdaQueryWrapper<ContactTag> tagQuery = new LambdaQueryWrapper<>();
        tagQuery.in(ContactTag::getTagId, tagIds);
        List<ContactTag> tags = contactTagMapper.selectList(tagQuery);

        return tags.stream().map(tag -> {
            TagVO vo = new TagVO();
            vo.setTagId(tag.getTagId());
            vo.setTagName(tag.getTagName());
            vo.setTagColor(tag.getTagColor());
            return vo;
        }).collect(Collectors.toList());
    }
}
