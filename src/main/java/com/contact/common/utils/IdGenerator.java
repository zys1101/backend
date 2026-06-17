package com.contact.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * ID生成工具类
 * 生成格式：前缀 + 9位数字
 *
 * @author Contact Manager
 */
public class IdGenerator {

    private static final AtomicLong USER_COUNTER = new AtomicLong(1);
    private static final AtomicLong CONTACT_COUNTER = new AtomicLong(1);
    private static final AtomicLong MATTER_COUNTER = new AtomicLong(1);
    private static final AtomicLong PICTURE_COUNTER = new AtomicLong(1);

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
