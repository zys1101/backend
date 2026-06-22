package com.contact.controller;

import com.contact.common.result.Result;
import com.contact.service.DashboardService;
import com.contact.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard仪表盘控制器
 *
 * @author Contact Manager
 */
@Tag(name = "Dashboard仪表盘", description = "Dashboard数据统计接口")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取Dashboard统计数据
     */
    @Operation(summary = "获取Dashboard数据", description = "获取仪表盘统计数据，包括统计卡片、图表数据、生日提醒等")
    @GetMapping("/stats")
    public Result<DashboardVO> getDashboardStats() {
        DashboardVO dashboardVO = dashboardService.getDashboardData();
        return Result.success(dashboardVO);
    }
}