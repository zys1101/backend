package com.contact.service;

import com.contact.vo.DashboardStatsVO;

/**
 * Dashboard 统计服务接口
 */
public interface DashboardService {

    /**
     * 获取 Dashboard 统计数据
     */
    DashboardStatsVO getDashboardStats();
}
