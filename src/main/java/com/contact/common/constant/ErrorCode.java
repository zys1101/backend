package com.contact.common.constant;

/**
 * 错误码常量
 *
 * @author Contact Manager
 */
public class ErrorCode {

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 参数校验失败
     */
    public static final int PARAM_VALID_ERROR = 400001;

    /**
     * 资源已存在
     */
    public static final int RESOURCE_EXISTS = 400002;

    /**
     * 资源不存在
     */
    public static final int RESOURCE_NOT_FOUND = 400003;

    /**
     * 业务规则违反
     */
    public static final int BUSINESS_RULE_ERROR = 400004;

    /**
     * Token无效
     */
    public static final int TOKEN_INVALID = 401001;

    /**
     * Token缺失
     */
    public static final int TOKEN_MISSING = 401002;

    /**
     * Token过期
     */
    public static final int TOKEN_EXPIRED = 401003;

    /**
     * 权限不足
     */
    public static final int PERMISSION_DENIED = 403001;

    /**
     * 请求过于频繁
     */
    public static final int REQUEST_TOO_FREQUENT = 429001;

    /**
     * 数据库错误
     */
    public static final int DATABASE_ERROR = 500001;

    /**
     * 文件上传错误
     */
    public static final int FILE_UPLOAD_ERROR = 500002;

    private ErrorCode() {
    }
}
