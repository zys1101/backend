package com.contact.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.entity.OperationLog;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {
    
    /**
     * 分页查询操作日志
     */
    Page<OperationLog> getOperationLogList(Integer page, Integer pageSize, String operationType, String username);
    
    /**
     * 记录操作日志
     */
    void logOperation(String operationType, String operationDesc, String requestUrl, String requestMethod, String requestParams);
}
