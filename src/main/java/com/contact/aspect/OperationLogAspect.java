package com.contact.aspect;

import com.contact.common.annotation.OperationLog;
import com.contact.common.utils.UserContext;
import com.contact.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * 操作日志AOP切面
 * 自动记录被 @OperationLog 注解标记的方法调用
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();
        String params = Arrays.toString(joinPoint.getArgs());

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("操作执行失败: {}", e.getMessage());
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            String desc = operationLog.desc();
            if (desc.isEmpty()) {
                desc = operationLog.value();
            }

            operationLogService.logOperation(
                    operationLog.value(),
                    desc,
                    requestUrl,
                    requestMethod,
                    params + " (耗时: " + (endTime - startTime) + "ms)"
            );
        }

        return result;
    }
}
