package com.contact.controller;

import com.contact.common.annotation.OperationLog;
import com.contact.common.result.Result;
import com.contact.dto.TagAssignDTO;
import com.contact.dto.TagCreateDTO;
import com.contact.service.TagService;
import com.contact.vo.ContactWithTagVO;
import com.contact.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签管理控制器
 */
@Tag(name = "标签管理", description = "标签的增删改查和关联接口")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 创建标签
     */
    @Operation(summary = "创建标签", description = "创建新的自定义标签")
    @PostMapping
    public Result<Map<String, String>> createTag(@Valid @RequestBody TagCreateDTO createDTO) {
        String tagId = tagService.createTag(createDTO);
        Map<String, String> data = new HashMap<>();
        data.put("tagId", tagId);
        return Result.success("创建成功", data);
    }

    /**
     * 更新标签
     */
    @Operation(summary = "更新标签", description = "更新标签名称和颜色")
    @PutMapping("/{tagId}")
    public Result<Void> updateTag(
            @Parameter(description = "标签ID") @PathVariable String tagId,
            @Valid @RequestBody TagCreateDTO updateDTO) {
        tagService.updateTag(tagId, updateDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 删除标签
     */
    @Operation(summary = "删除标签", description = "删除标签及其关联关系")
    @DeleteMapping("/{tagId}")
    public Result<Void> deleteTag(
            @Parameter(description = "标签ID") @PathVariable String tagId) {
        tagService.deleteTag(tagId);
        return Result.success("删除成功", null);
    }

    /**
     * 获取用户所有标签（带统计）
     */
    @Operation(summary = "获取用户所有标签", description = "获取当前用户的所有标签及关联联系人数量")
    @GetMapping
    public Result<List<TagVO>> getUserTags() {
        List<TagVO> tags = tagService.getUserTagsWithStats();
        return Result.success(tags);
    }

    /**
     * 为联系人分配标签
     */
    @Operation(summary = "为联系人分配标签", description = "批量为联系人分配标签")
    @PostMapping("/contacts/{ctId}/tags")
    public Result<Void> assignTagsToContact(
            @Parameter(description = "联系人ID") @PathVariable String ctId,
            @Valid @RequestBody TagAssignDTO assignDTO) {
        tagService.assignTagsToContact(ctId, assignDTO);
        return Result.success("分配成功", null);
    }

    /**
     * 移除联系人标签
     */
    @Operation(summary = "移除联系人标签", description = "移除联系人的指定标签")
    @DeleteMapping("/contacts/{ctId}/tags/{tagId}")
    public Result<Void> removeTagFromContact(
            @Parameter(description = "联系人ID") @PathVariable String ctId,
            @Parameter(description = "标签ID") @PathVariable String tagId) {
        tagService.removeTagFromContact(ctId, tagId);
        return Result.success("移除成功", null);
    }

    /**
     * 根据标签筛选联系人
     */
    @Operation(summary = "根据标签筛选联系人", description = "获取指定标签下的所有联系人")
    @GetMapping("/contacts")
    public Result<List<ContactWithTagVO>> getContactsByTagId(
            @Parameter(description = "标签ID") @RequestParam String tagId) {
        List<ContactWithTagVO> contacts = tagService.getContactsByTagId(tagId);
        return Result.success(contacts);
    }

    /**
     * 获取联系人详情（含标签）
     */
    @Operation(summary = "获取联系人详情（含标签）", description = "获取联系人详细信息及其标签")
    @GetMapping("/contacts/{ctId}")
    public Result<ContactWithTagVO> getContactDetailWithTags(
            @Parameter(description = "联系人ID") @PathVariable String ctId) {
        ContactWithTagVO vo = tagService.getContactDetailWithTags(ctId);
        return Result.success(vo);
    }
}