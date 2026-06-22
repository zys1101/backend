package com.contact.service;

import com.contact.vo.DashboardVO;

/**
 * Dashboard仪表盘服务接口
 *
 * @author Contact Manager
 */
public interface DashboardService {

    /**
     * 获取Dashboard统计数据
     *
     * @return Dashboard数据
     */
    DashboardVO getDashboardData();
}