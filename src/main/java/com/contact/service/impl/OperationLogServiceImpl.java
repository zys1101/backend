package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.utils.IdGenerator;
import com.contact.common.utils.UserContext;
import com.contact.entity.OperationLog;
import com.contact.entity.UserInfo;
import com.contact.mapper.OperationLogMapper;
import com.contact.mapper.UserInfoMapper;
import com.contact.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final UserInfoMapper userInfoMapper;

    @Override
    public Page<OperationLog> getOperationLogList(Integer page, Integer pageSize, String operationType, String username) {
        LambdaQueryWrapper<OperationLog> query = new LambdaQueryWrapper<>();

        // 只查询当前用户的操作日志
        query.eq(OperationLog::getUserId, UserContext.getUserId());

        if (StringUtils.hasText(operationType)) {
            query.eq(OperationLog::getOperationType, operationType);
        }

        if (StringUtils.hasText(username)) {
            query.like(OperationLog::getUsername, username);
        }

        query.orderByDesc(OperationLog::getOperationTime);

        return operationLogMapper.selectPage(new Page<>(page, pageSize), query);
    }

    @Override
    public void logOperation(String operationType, String operationDesc, String requestUrl, String requestMethod, String requestParams) {
        try {
            OperationLog opLog = new OperationLog();
            opLog.setLogId(IdGenerator.generateLogId());
            opLog.setUserId(UserContext.getUserId());
            opLog.setOperationType(operationType);
            opLog.setOperationDesc(operationDesc);
            opLog.setRequestUrl(requestUrl);
            opLog.setRequestMethod(requestMethod);
            opLog.setOperationTime(LocalDateTime.now());

            // 获取当前用户名
            UserInfo user = userInfoMapper.selectById(UserContext.getUserId());
            if (user != null) {
                opLog.setUsername(user.getUsername());
            }

            // 限制参数长度
            if (StringUtils.hasText(requestParams) && requestParams.length() > 1000) {
                opLog.setRequestParams(requestParams.substring(0, 1000));
            } else {
                opLog.setRequestParams(requestParams);
            }

            operationLogMapper.insert(opLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage(), e);
        }
    }
}
