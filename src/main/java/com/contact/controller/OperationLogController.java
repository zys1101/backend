package com.contact.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.result.Result;
import com.contact.entity.OperationLog;
import com.contact.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "操作日志", description = "系统操作日志查询接口")
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "获取操作日志列表", description = "分页查询当前用户的操作日志")
    @GetMapping
    public Result<Map<String, Object>> getOperationLogList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "操作类型筛选") @RequestParam(required = false) String operationType,
            @Parameter(description = "用户名筛选") @RequestParam(required = false) String username) {

        Page<OperationLog> logPage = operationLogService.getOperationLogList(page, pageSize, operationType, username);

        Map<String, Object> data = new HashMap<>();
        data.put("list", logPage.getRecords());
        data.put("total", logPage.getTotal());
        data.put("page", logPage.getCurrent());
        data.put("pageSize", logPage.getSize());
        data.put("totalPages", logPage.getPages());

        return Result.success(data);
    }
}