package com.contact.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.annotation.OperationLog;
import com.contact.common.result.PageResult;
import com.contact.common.result.Result;
import com.contact.dto.ContactCreateDTO;
import com.contact.dto.ContactUpdateDTO;
import com.contact.service.ContactService;
import com.contact.vo.ContactDetailVO;
import com.contact.vo.ContactVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import com.contact.dto.ContactCreateDTO;
import com.contact.dto.ContactUpdateDTO;
import com.contact.service.ContactService;
import com.contact.vo.ContactDetailVO;
import com.contact.vo.ContactVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 联系人控制器
 *
 * @author Contact Manager
 */
@Tag(name = "联系人管理", description = "联系人增删改查、头像上传、黑名单等接口")
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * 获取联系人列表
     */
    @Operation(summary = "获取联系人列表", description = "分页查询联系人列表，支持搜索、筛选、排序")
    @GetMapping
    public Result<PageResult<ContactVO>> getContactList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "姓名搜索") @RequestParam(required = false) String ctName,
            @Parameter(description = "电话搜索") @RequestParam(required = false) String ctPhone,
            @Parameter(description = "性别筛选") @RequestParam(required = false) String ctMf,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序方向") @RequestParam(required = false) String sortOrder) {

        Page<ContactVO> contactPage = contactService.getContactList(
                page, pageSize, ctName, ctPhone, ctMf, sortBy, sortOrder);

        PageResult<ContactVO> pageResult = new PageResult<>(
                contactPage.getRecords(),
                contactPage.getTotal(),
                (int) contactPage.getCurrent(),
                (int) contactPage.getSize()
        );

        return Result.success(pageResult);
    }

    /**
     * 获取联系人详情
     */
    @Operation(summary = "获取联系人详情", description = "根据ID获取联系人详细信息")
    @GetMapping("/{id}")
    public Result<ContactDetailVO> getContactDetail(
            @Parameter(description = "联系人ID") @PathVariable String id) {

        ContactDetailVO detailVO = contactService.getContactDetail(id);
        return Result.success(detailVO);
    }

    /**
     * 新增联系人
     */
    @Operation(summary = "新增联系人", description = "创建新的联系人")
    @OperationLog(value = "新增", desc = "新增联系人")
    @PostMapping
    public Result<Map<String, String>> createContact(@Valid @RequestBody ContactCreateDTO createDTO) {
        String ctId = contactService.createContact(createDTO);

        Map<String, String> data = new HashMap<>();
        data.put("ctId", ctId);

        return Result.success("创建成功", data);
    }

    /**
     * 更新联系人
     */
    @Operation(summary = "更新联系人", description = "更新联系人信息")
    @OperationLog(value = "修改", desc = "修改联系人")
    @PutMapping("/{id}")
    public Result<Void> updateContact(
            @Parameter(description = "联系人ID") @PathVariable String id,
            @Valid @RequestBody ContactUpdateDTO updateDTO) {

        contactService.updateContact(id, updateDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除联系人
     */
    @Operation(summary = "删除联系人", description = "删除联系人及其相关数据")
    @OperationLog(value = "删除", desc = "删除联系人")
    @DeleteMapping("/{id}")
    public Result<Void> deleteContact(
            @Parameter(description = "联系人ID") @PathVariable String id) {

        contactService.deleteContact(id);
        return Result.success("删除成功", null);
    }

    /**
     * 上传头像
     */
    @Operation(summary = "上传头像", description = "上传联系人头像，支持jpg/png/jpeg格式，最大5MB")
    @PostMapping("/{id}/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @Parameter(description = "联系人ID") @PathVariable String id,
            @Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {

        String avatarUrl = contactService.uploadAvatar(id, file);

        Map<String, String> data = new HashMap<>();
        data.put("avatar", avatarUrl);

        return Result.success("上传成功", data);
    }

    /**
     * 加入黑名单
     */
    @Operation(summary = "加入黑名单", description = "将联系人加入黑名单")
    @OperationLog(value = "拉黑", desc = "将联系人加入黑名单")
    @PostMapping("/{id}/blacklist")
    public Result<Void> addToBlacklist(
            @Parameter(description = "联系人ID") @PathVariable String id) {

        contactService.addToBlacklist(id);
        return Result.success("已加入黑名单", null);
    }
}
