package com.contact.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ID生成工具类
 * 生成格式：前缀 + 9位数字
 * <p>
 * 重要：应用重启后计数器会从1开始，必须在启动时通过 {@link #initFromExistingMaxId}
 * 从数据库已有数据初始化计数器的起始值，避免主键冲突。
 *
 * @author Contact Manager
 */
public class IdGenerator {

    // 非final，允许启动时从DB初始化
    private static final AtomicLong USER_COUNTER = new AtomicLong(1);
    private static final AtomicLong CONTACT_COUNTER = new AtomicLong(1);
    private static final AtomicLong MATTER_COUNTER = new AtomicLong(1);
    private static final AtomicLong PICTURE_COUNTER = new AtomicLong(1);
    private static final AtomicLong TAG_COUNTER = new AtomicLong(1);

    /**
     * 根据数据库中已有的最大ID初始化计数器，避免重启后生成重复ID。
     * 应在应用启动时、插入任何数据之前调用。
     *
     * @param maxUserId    数据库中最大用户ID（如 "U000000004"），无记录时传 null
     * @param maxContactId 数据库中最大联系人ID
     * @param maxMatterId  数据库中最大事项ID
     * @param maxPictureId 数据库中最大图片ID
     * @param maxTagId     数据库中最大标签ID
     */
    public static void initFromExistingMaxId(String maxUserId, String maxContactId,
                                              String maxMatterId, String maxPictureId,
                                              String maxTagId) {
        initCounter(USER_COUNTER, maxUserId);
        initCounter(CONTACT_COUNTER, maxContactId);
        initCounter(MATTER_COUNTER, maxMatterId);
        initCounter(PICTURE_COUNTER, maxPictureId);
        initCounter(TAG_COUNTER, maxTagId);
    }
    
    /**
     * 重载方法，兼容旧调用（不含tagId）
     */
    public static void initFromExistingMaxId(String maxUserId, String maxContactId,
                                              String maxMatterId, String maxPictureId) {
        initFromExistingMaxId(maxUserId, maxContactId, maxMatterId, maxPictureId, null);
    }

    private static void initCounter(AtomicLong counter, String maxId) {
        if (maxId == null || maxId.length() < 2) {
            return;
        }
        try {
            // 去掉前缀字母，取数字部分
            String numStr = maxId.substring(1);
            long maxNum = Long.parseLong(numStr);
            // counter设为 max+1，保证下次生成的值大于已有最大值
            counter.set(maxNum + 1);
        } catch (NumberFormatException e) {
            // 解析失败保持默认值
        }
    }

    /**
     * 生成用户ID
     * 格式：U000000001
     */
    public static String generateUserId() {
        return generateId("U", USER_COUNTER);
    }

    /**
     * 生成联系人ID
     * 格式：C000000001
     */
    public static String generateContactId() {
        return generateId("C", CONTACT_COUNTER);
    }

    /**
     * 生成事项ID
     * 格式：M000000001
     */
    public static String generateMatterId() {
        return generateId("M", MATTER_COUNTER);
    }

    /**
     * 生成图片ID
     * 格式：P000000001
     */
    public static String generatePictureId() {
        return generateId("P", PICTURE_COUNTER);
    }

    /**
     * 生成标签ID
     * 格式：T000000001
     */
    public static String generateTagId() {
        return generateId("T", TAG_COUNTER);
    }

    /**
     * 生成日志ID
     * 格式：L000000001
     */
    public static String generateLogId() {
        return generateId("L", new java.util.concurrent.atomic.AtomicLong(1));
    }

    /**
     * 生成ID
     *
     * @param prefix  前缀
     * @param counter 计数器
     * @return ID字符串
     */
    private static String generateId(String prefix, AtomicLong counter) {
        long num = counter.getAndIncrement();
        return prefix + String.format("%09d", num);
    }

    private IdGenerator() {
    }
}
