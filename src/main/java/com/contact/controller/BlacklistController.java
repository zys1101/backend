package com.contact.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.annotation.OperationLog;
import com.contact.common.result.PageResult;
import com.contact.common.result.Result;
import com.contact.service.ContactService;
import com.contact.vo.ContactVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 黑名单控制器
 *
 * @author Contact Manager
 */
@Tag(name = "黑名单管理", description = "黑名单列表、恢复联系人等接口")
@RestController
@RequestMapping("/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final ContactService contactService;

    /**
     * 获取黑名单列表
     */
    @Operation(summary = "获取黑名单列表", description = "分页查询黑名单列表，支持搜索、筛选、排序")
    @GetMapping
    public Result<PageResult<ContactVO>> getBlacklist(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "姓名搜索") @RequestParam(required = false) String ctName,
            @Parameter(description = "电话搜索") @RequestParam(required = false) String ctPhone,
            @Parameter(description = "性别筛选") @RequestParam(required = false) String ctMf,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序方向") @RequestParam(required = false) String sortOrder) {

        Page<ContactVO> contactPage = contactService.getBlacklist(
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
     * 恢复联系人
     */
    @OperationLog(value = "恢复", desc = "从黑名单中恢复联系人")
    @Operation(summary = "恢复联系人", description = "将联系人从黑名单中恢复")
    @DeleteMapping("/{id}")
    public Result<Void> restoreContact(
            @Parameter(description = "联系人ID") @PathVariable String id) {

        contactService.restoreContact(id);
        return Result.success("恢复成功", null);
    }
}
