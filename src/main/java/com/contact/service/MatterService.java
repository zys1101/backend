package com.contact.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.dto.MatterCreateDTO;
import com.contact.dto.MatterUpdateDTO;
import com.contact.vo.MatterVO;

/**
 * 事项服务接口
 *
 * @author Contact Manager
 */
public interface MatterService {

    /**
     * 分页查询事项列表
     *
     * @param page         页码
     * @param pageSize     每页条数
     * @param matter       事项内容搜索
     * @param matterDelete 状态筛选
     * @param ctId         联系人ID筛选
     * @param sortBy       排序字段
     * @param sortOrder    排序方向
     * @return 分页结果
     */
    Page<MatterVO> getMatterList(Integer page, Integer pageSize, String matter,
                                  Integer matterDelete, String ctId, String sortBy, String sortOrder);

    /**
     * 获取事项详情
     *
     * @param matterId 事项ID
     * @return 事项详情
     */
    MatterVO getMatterDetail(String matterId);

    /**
     * 新增事项
     *
     * @param createDTO 新增信息
     * @return 事项ID
     */
    String createMatter(MatterCreateDTO createDTO);

    /**
     * 更新事项
     *
     * @param matterId   事项ID
     * @param updateDTO 更新信息
     */
    void updateMatter(String matterId, MatterUpdateDTO updateDTO);

    /**
     * 删除事项
     *
     * @param matterId 事项ID
     */
    void deleteMatter(String matterId);

    /**
     * 完成事项
     *
     * @param matterId 事项ID
     */
    void completeMatter(String matterId);

    /**
     * 取消事项
     *
     * @param matterId 事项ID
     */
    void cancelMatter(String matterId);

    /**
     * 重新打开事项（将已取消/已完成的事项恢复为待完成状态）
     *
     * @param matterId 事项ID
     */
    void reopenMatter(String matterId);
}
