package com.contact.controller;

import com.contact.common.result.Result;
import com.contact.service.DashboardService;
import com.contact.vo.DashboardStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard控制器
 */
@Tag(name = "Dashboard", description = "数据统计看板接口")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取Dashboard统计数据
     */
    @Operation(summary = "获取Dashboard统计数据", description = "获取联系人和事项的整体统计数据")
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getDashboardStats() {
        DashboardStatsVO stats = dashboardService.getDashboardStats();
        return Result.success(stats);
    }
}
