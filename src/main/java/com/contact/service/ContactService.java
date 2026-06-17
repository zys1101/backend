package com.contact.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.dto.ContactCreateDTO;
import com.contact.dto.ContactUpdateDTO;
import com.contact.vo.ContactDetailVO;
import com.contact.vo.ContactVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 联系人服务接口
 *
 * @author Contact Manager
 */
public interface ContactService {

    /**
     * 分页查询联系人列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param ctName   姓名搜索
     * @param ctPhone  电话搜索
     * @param ctMf     性别筛选
     * @param sortBy   排序字段
     * @param sortOrder 排序方向
     * @return 分页结果
     */
    Page<ContactVO> getContactList(Integer page, Integer pageSize, String ctName, 
                                    String ctPhone, String ctMf, String sortBy, String sortOrder);

    /**
     * 获取联系人详情
     *
     * @param ctId 联系人ID
     * @return 联系人详情
     */
    ContactDetailVO getContactDetail(String ctId);

    /**
     * 新增联系人
     *
     * @param createDTO 新增信息
     * @return 联系人ID
     */
    String createContact(ContactCreateDTO createDTO);

    /**
     * 更新联系人
     *
     * @param ctId       联系人ID
     * @param updateDTO 更新信息
     */
    void updateContact(String ctId, ContactUpdateDTO updateDTO);

    /**
     * 删除联系人
     *
     * @param ctId 联系人ID
     */
    void deleteContact(String ctId);

    /**
     * 上传头像
     *
     * @param ctId 联系人ID
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(String ctId, MultipartFile file);

    /**
     * 加入黑名单
     *
     * @param ctId 联系人ID
     */
    void addToBlacklist(String ctId);

    /**
     * 恢复联系人
     *
     * @param ctId 联系人ID
     */
    void restoreContact(String ctId);

    /**
     * 分页查询黑名单列表
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param ctName   姓名搜索
     * @param ctPhone  电话搜索
     * @param ctMf     性别筛选
     * @param sortBy   排序字段
     * @param sortOrder 排序方向
     * @return 分页结果
     */
    Page<ContactVO> getBlacklist(Integer page, Integer pageSize, String ctName,
                                  String ctPhone, String ctMf, String sortBy, String sortOrder);
}
