package com.contact.common.constant;

/**
 * 系统常量
 *
 * @author Contact Manager
 */
public class SystemConstants {

    /**
     * 联系人状态 - 正常
     */
    public static final int CONTACT_STATUS_NORMAL = 0;

    /**
     * 联系人状态 - 黑名单
     */
    public static final int CONTACT_STATUS_BLACKLIST = 1;

    /**
     * 事项状态 - 待完成
     */
    public static final int MATTER_STATUS_PENDING = 0;

    /**
     * 事项状态 - 已取消
     */
    public static final int MATTER_STATUS_CANCELLED = 1;

    /**
     * 事项状态 - 已完成
     */
    public static final int MATTER_STATUS_COMPLETED = 2;

    /**
     * 用户状态 - 禁用
     */
    public static final int USER_STATUS_DISABLED = 0;

    /**
     * 用户状态 - 启用
     */
    public static final int USER_STATUS_ENABLED = 1;

    /**
     * 性别 - 男
     */
    public static final String GENDER_MALE = "男";

    /**
     * 性别 - 女
     */
    public static final String GENDER_FEMALE = "女";

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 1;

    /**
     * 默认每页条数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页条数
     */
    public static final int MAX_PAGE_SIZE = 50;

    /**
     * ID前缀 - 用户
     */
    public static final String ID_PREFIX_USER = "U";

    /**
     * ID前缀 - 联系人
     */
    public static final String ID_PREFIX_CONTACT = "C";

    /**
     * ID前缀 - 事项
     */
    public static final String ID_PREFIX_MATTER = "M";

    /**
     * ID前缀 - 图片
     */
    public static final String ID_PREFIX_PICTURE = "P";

    private SystemConstants() {
    }
}
