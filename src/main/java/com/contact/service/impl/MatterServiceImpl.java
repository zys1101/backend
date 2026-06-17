package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.constant.ErrorCode;
import com.contact.common.constant.SystemConstants;
import com.contact.common.exception.BusinessException;
import com.contact.common.utils.IdGenerator;
import com.contact.common.utils.UserContext;
import com.contact.dto.MatterCreateDTO;
import com.contact.dto.MatterUpdateDTO;
import com.contact.entity.Contact;
import com.contact.entity.Matter;
import com.contact.mapper.ContactMapper;
import com.contact.mapper.MatterMapper;
import com.contact.service.MatterService;
import com.contact.vo.MatterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 事项服务实现类
 *
 * @author Contact Manager
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatterServiceImpl implements MatterService {

    private final MatterMapper matterMapper;
    private final ContactMapper contactMapper;

    @Override
    public Page<MatterVO> getMatterList(Integer page, Integer pageSize, String matter,
                                         Integer matterDelete, String ctId, String sortBy, String sortOrder) {
        String userId = UserContext.getUserId();
        log.info("查询事项列表: userId={}, page={}, pageSize={}", userId, page, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Matter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Matter::getUserId, userId);

        // 事项内容模糊搜索
        if (StringUtils.hasText(matter)) {
            queryWrapper.like(Matter::getMatter, matter);
        }

        // 状态筛选
        if (matterDelete != null) {
            queryWrapper.eq(Matter::getMatterDelete, matterDelete);
        }

        // 联系人ID筛选
        if (StringUtils.hasText(ctId)) {
            queryWrapper.eq(Matter::getCtId, ctId);
        }

        // 排序
        if (StringUtils.hasText(sortBy)) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            switch (sortBy) {
                case "matterTime":
                    queryWrapper.orderBy(true, isAsc, Matter::getMatterTime);
                    break;
                case "createdAt":
                    queryWrapper.orderBy(true, isAsc, Matter::getCreatedAt);
                    break;
                default:
                    queryWrapper.orderByDesc(Matter::getCreatedAt);
            }
        } else {
            queryWrapper.orderByDesc(Matter::getCreatedAt);
        }

        // 分页查询
        Page<Matter> matterPage = matterMapper.selectPage(
                new Page<>(page, pageSize), queryWrapper);

        // 转换为VO
        Page<MatterVO> voPage = new Page<>();
        voPage.setCurrent(matterPage.getCurrent());
        voPage.setSize(matterPage.getSize());
        voPage.setTotal(matterPage.getTotal());
        voPage.setPages(matterPage.getPages());

        List<MatterVO> voList = matterPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public MatterVO getMatterDetail(String matterId) {
        String userId = UserContext.getUserId();
        log.info("查询事项详情: userId={}, matterId={}", userId, matterId);

        Matter matter = matterMapper.selectById(matterId);
        if (matter == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "事项不存在");
        }

        // 验证权限
        if (!userId.equals(matter.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权访问此事项");
        }

        return convertToVO(matter);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createMatter(MatterCreateDTO createDTO) {
        String userId = UserContext.getUserId();
        log.info("新增事项: userId={}, ctId={}", userId, createDTO.getCtId());

        // 验证联系人是否存在
        Contact contact = contactMapper.selectById(createDTO.getCtId());
        if (contact == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "联系人不存在");
        }

        // 验证权限
        if (!userId.equals(contact.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权为此联系人添加事项");
        }

        // 创建事项
        Matter matter = new Matter();
        matter.setMatterId(IdGenerator.generateMatterId());
        matter.setCtId(createDTO.getCtId());
        matter.setUserId(userId);
        matter.setMatterTime(createDTO.getMatterTime());
        matter.setMatter(createDTO.getMatter());
        matter.setMatterDelete(SystemConstants.MATTER_STATUS_PENDING);
        matter.setCreatedAt(LocalDateTime.now());
        matter.setUpdatedAt(LocalDateTime.now());

        matterMapper.insert(matter);
        log.info("事项创建成功: matterId={}", matter.getMatterId());

        return matter.getMatterId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMatter(String matterId, MatterUpdateDTO updateDTO) {
        String userId = UserContext.getUserId();
        log.info("更新事项: userId={}, matterId={}", userId, matterId);

        Matter matter = matterMapper.selectById(matterId);
        if (matter == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "事项不存在");
        }

        // 验证权限
        if (!userId.equals(matter.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权修改此事项");
        }

        // 更新事项信息
        if (updateDTO.getMatterTime() != null) {
            matter.setMatterTime(updateDTO.getMatterTime());
        }
        if (StringUtils.hasText(updateDTO.getMatter())) {
            matter.setMatter(updateDTO.getMatter());
        }
        matter.setUpdatedAt(LocalDateTime.now());

        matterMapper.updateById(matter);
        log.info("事项更新成功: matterId={}", matterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMatter(String matterId) {
        String userId = UserContext.getUserId();
        log.info("删除事项: userId={}, matterId={}", userId, matterId);

        Matter matter = matterMapper.selectById(matterId);
        if (matter == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "事项不存在");
        }

        // 验证权限
        if (!userId.equals(matter.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权删除此事项");
        }

        matterMapper.deleteById(matterId);
        log.info("事项删除成功: matterId={}", matterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeMatter(String matterId) {
        String userId = UserContext.getUserId();
        log.info("完成事项: userId={}, matterId={}", userId, matterId);

        Matter matter = matterMapper.selectById(matterId);
        if (matter == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "事项不存在");
        }

        // 验证权限
        if (!userId.equals(matter.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权操作此事项");
        }

        // 更新状态
        matter.setMatterDelete(SystemConstants.MATTER_STATUS_COMPLETED);
        matter.setUpdatedAt(LocalDateTime.now());
        matterMapper.updateById(matter);

        log.info("事项完成成功: matterId={}", matterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMatter(String matterId) {
        String userId = UserContext.getUserId();
        log.info("取消事项: userId={}, matterId={}", userId, matterId);

        Matter matter = matterMapper.selectById(matterId);
        if (matter == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "事项不存在");
        }

        // 验证权限
        if (!userId.equals(matter.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权操作此事项");
        }

        // 更新状态
        matter.setMatterDelete(SystemConstants.MATTER_STATUS_CANCELLED);
        matter.setUpdatedAt(LocalDateTime.now());
        matterMapper.updateById(matter);

        log.info("事项取消成功: matterId={}", matterId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reopenMatter(String matterId) {
        String userId = UserContext.getUserId();
        log.info("重新打开事项: userId={}, matterId={}", userId, matterId);

        Matter matter = matterMapper.selectById(matterId);
        if (matter == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "事项不存在");
        }

        // 验证权限
        if (!userId.equals(matter.getUserId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "无权操作此事项");
        }

        // 恢复为待完成状态
        matter.setMatterDelete(SystemConstants.MATTER_STATUS_PENDING);
        matter.setUpdatedAt(LocalDateTime.now());
        matterMapper.updateById(matter);

        log.info("事项重新打开成功: matterId={}", matterId);
    }

    /**
     * 转换为VO
     */
    private MatterVO convertToVO(Matter matter) {
        MatterVO vo = new MatterVO();
        BeanUtils.copyProperties(matter, vo);

        // 获取联系人姓名
        Contact contact = contactMapper.selectById(matter.getCtId());
        if (contact != null) {
            vo.setContactName(contact.getCtName());
        }

        return vo;
    }
}
