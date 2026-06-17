package com.contact.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.result.PageResult;
import com.contact.common.result.Result;
import com.contact.dto.MatterCreateDTO;
import com.contact.dto.MatterUpdateDTO;
import com.contact.service.MatterService;
import com.contact.vo.MatterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 事项提醒控制器
 *
 * @author Contact Manager
 */
@Tag(name = "事项提醒", description = "事项增删改查、完成、取消等接口")
@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final MatterService matterService;

    /**
     * 获取事项列表
     */
    @Operation(summary = "获取事项列表", description = "分页查询事项列表，支持搜索、筛选、排序")
    @GetMapping
    public Result<PageResult<MatterVO>> getMatterList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "事项内容搜索") @RequestParam(required = false) String matter,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Integer matterDelete,
            @Parameter(description = "联系人ID筛选") @RequestParam(required = false) String ctId,
            @Parameter(description = "排序字段") @RequestParam(required = false) String sortBy,
            @Parameter(description = "排序方向") @RequestParam(required = false) String sortOrder) {

        Page<MatterVO> matterPage = matterService.getMatterList(
                page, pageSize, matter, matterDelete, ctId, sortBy, sortOrder);

        PageResult<MatterVO> pageResult = new PageResult<>(
                matterPage.getRecords(),
                matterPage.getTotal(),
                (int) matterPage.getCurrent(),
                (int) matterPage.getSize()
        );

        return Result.success(pageResult);
    }

    /**
     * 获取事项详情
     */
    @Operation(summary = "获取事项详情", description = "根据ID获取事项详细信息")
    @GetMapping("/{id}")
    public Result<MatterVO> getMatterDetail(
            @Parameter(description = "事项ID") @PathVariable String id) {

        MatterVO matterVO = matterService.getMatterDetail(id);
        return Result.success(matterVO);
    }

    /**
     * 新增事项
     */
    @Operation(summary = "新增事项", description = "创建新的事项提醒")
    @PostMapping
    public Result<Map<String, String>> createMatter(@Valid @RequestBody MatterCreateDTO createDTO) {
        String matterId = matterService.createMatter(createDTO);

        Map<String, String> data = new HashMap<>();
        data.put("matterId", matterId);

        return Result.success("创建成功", data);
    }

    /**
     * 更新事项
     */
    @Operation(summary = "更新事项", description = "更新事项信息")
    @PutMapping("/{id}")
    public Result<Void> updateMatter(
            @Parameter(description = "事项ID") @PathVariable String id,
            @Valid @RequestBody MatterUpdateDTO updateDTO) {

        matterService.updateMatter(id, updateDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除事项
     */
    @Operation(summary = "删除事项", description = "删除事项")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMatter(
            @Parameter(description = "事项ID") @PathVariable String id) {

        matterService.deleteMatter(id);
        return Result.success("删除成功", null);
    }

    /**
     * 完成事项
     */
    @Operation(summary = "完成事项", description = "将事项标记为已完成")
    @PutMapping("/{id}/complete")
    public Result<Void> completeMatter(
            @Parameter(description = "事项ID") @PathVariable String id) {

        matterService.completeMatter(id);
        return Result.success("事项已完成", null);
    }

    /**
     * 取消事项
     */
    @Operation(summary = "取消事项", description = "将事项标记为已取消")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelMatter(
            @Parameter(description = "事项ID") @PathVariable String id) {

        matterService.cancelMatter(id);
        return Result.success("事项已取消", null);
    }

    /**
     * 重新打开事项
     */
    @Operation(summary = "重新打开事项", description = "将已取消或已完成的事项恢复为待完成状态")
    @PutMapping("/{id}/reopen")
    public Result<Void> reopenMatter(
            @Parameter(description = "事项ID") @PathVariable String id) {

        matterService.reopenMatter(id);
        return Result.success("事项已重新打开", null);
    }
}
