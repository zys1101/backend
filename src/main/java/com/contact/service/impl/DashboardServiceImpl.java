package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contact.common.constant.SystemConstants;
import com.contact.common.utils.UserContext;
import com.contact.entity.Contact;
import com.contact.entity.Matter;
import com.contact.mapper.ContactMapper;
import com.contact.mapper.MatterMapper;
import com.contact.service.DashboardService;
import com.contact.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard仪表盘服务实现类
 *
 * @author Contact Manager
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ContactMapper contactMapper;
    private final MatterMapper matterMapper;

    @Override
    public DashboardVO getDashboardData() {
        String userId = UserContext.getUserId();
        log.info("获取Dashboard数据: userId={}", userId);

        DashboardVO vo = new DashboardVO();

        // 1. 统计卡片
        vo.setStatsCard(buildStatsCard(userId));

        // 2. 性别分布
        vo.setGenderDistribution(buildGenderDistribution(userId));

        // 3. 事项完成率
        vo.setMatterCompletion(buildCompletionRate(userId));

        // 4. 联系人增长趋势（最近6个月）
        vo.setContactGrowth(buildContactGrowth(userId));

        // 5. 最近生日提醒
        vo.setBirthdayReminders(buildBirthdayReminders(userId));

        return vo;
    }

    /**
     * 构建统计卡片数据
     */
    private DashboardVO.StatsCard buildStatsCard(String userId) {
        DashboardVO.StatsCard card = new DashboardVO.StatsCard();

        // 联系人总数（正常状态）
        LambdaQueryWrapper<Contact> contactQuery = new LambdaQueryWrapper<>();
        contactQuery.eq(Contact::getUserId, userId)
                    .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL);
        card.setTotalContacts(contactMapper.selectCount(contactQuery));

        // 黑名单人数
        LambdaQueryWrapper<Contact> blacklistQuery = new LambdaQueryWrapper<>();
        blacklistQuery.eq(Contact::getUserId, userId)
                      .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_BLACKLIST);
        card.setBlacklistCount(contactMapper.selectCount(blacklistQuery));

        // 待完成事项数
        LambdaQueryWrapper<Matter> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Matter::getUserId, userId)
                    .eq(Matter::getMatterDelete, SystemConstants.MATTER_STATUS_PENDING);
        card.setPendingMatters(matterMapper.selectCount(pendingQuery));

        // 已完成事项数
        LambdaQueryWrapper<Matter> completedQuery = new LambdaQueryWrapper<>();
        completedQuery.eq(Matter::getUserId, userId)
                      .eq(Matter::getMatterDelete, SystemConstants.MATTER_STATUS_COMPLETED);
        card.setCompletedMatters(matterMapper.selectCount(completedQuery));

        // 本月生日人数——仅统计正常联系人
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        List<Contact> contacts = contactMapper.selectList(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL)
                        .isNotNull(Contact::getCtBirth)
        );
        long birthdayThisMonth = contacts.stream()
                .filter(c -> c.getCtBirth() != null && c.getCtBirth().getMonthValue() == currentMonth)
                .count();
        card.setBirthdayThisMonth(birthdayThisMonth);

        return card;
    }

    /**
     * 构建性别分布
     */
    private List<DashboardVO.ChartItem> buildGenderDistribution(String userId) {
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId)
                    .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL);

        List<Contact> contacts = contactMapper.selectList(queryWrapper);

        long maleCount = contacts.stream()
                .filter(c -> SystemConstants.GENDER_MALE.equals(c.getCtMf()))
                .count();
        long femaleCount = contacts.stream()
                .filter(c -> SystemConstants.GENDER_FEMALE.equals(c.getCtMf()))
                .count();

        List<DashboardVO.ChartItem> list = new ArrayList<>();
        list.add(new DashboardVO.ChartItem("男", maleCount));
        list.add(new DashboardVO.ChartItem("女", femaleCount));
        return list;
    }

    /**
     * 构建事项完成率
     */
    private DashboardVO.CompletionRate buildCompletionRate(String userId) {
        LambdaQueryWrapper<Matter> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.eq(Matter::getUserId, userId);
        long total = matterMapper.selectCount(totalQuery);

        LambdaQueryWrapper<Matter> completedQuery = new LambdaQueryWrapper<>();
        completedQuery.eq(Matter::getUserId, userId)
                      .eq(Matter::getMatterDelete, SystemConstants.MATTER_STATUS_COMPLETED);
        long completed = matterMapper.selectCount(completedQuery);

        DashboardVO.CompletionRate rate = new DashboardVO.CompletionRate();
        rate.setCompleted(completed);
        rate.setUncompleted(total - completed);
        rate.setPercentage(total > 0 ? Math.round((double) completed / total * 10000.0) / 100.0 : 0.0);
        return rate;
    }

    /**
     * 构建联系人增长趋势（最近6个月）
     */
    private List<DashboardVO.GrowthItem> buildContactGrowth(String userId) {
        LocalDate now = LocalDate.now();
        List<DashboardVO.GrowthItem> items = new ArrayList<>();

        // 获取该用户所有联系人
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contact::getUserId, userId)
                    .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL);
        List<Contact> contacts = contactMapper.selectList(queryWrapper);

        // 按月统计
        Map<String, Long> monthCountMap = contacts.stream()
                .filter(c -> c.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        c -> YearMonth.from(c.getCreatedAt()).toString(),
                        Collectors.counting()
                ));

        // 填充最近6个月
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.from(now.minusMonths(i));
            String monthStr = ym.toString();
            items.add(new DashboardVO.GrowthItem(monthStr, monthCountMap.getOrDefault(monthStr, 0L)));
        }

        return items;
    }

    /**
     * 构建最近生日提醒
     */
    private List<DashboardVO.BirthdayReminder> buildBirthdayReminders(String userId) {
        // 获取所有有生日的正常联系人
        List<Contact> contacts = contactMapper.selectList(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getUserId, userId)
                        .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL)
                        .isNotNull(Contact::getCtBirth)
        );

        LocalDate today = LocalDate.now();

        List<DashboardVO.BirthdayReminder> reminders = contacts.stream()
                .map(c -> {
                    LocalDate birth = c.getCtBirth();
                    // 计算今年生日
                    LocalDate thisYearBirthday = birth.withYear(today.getYear());
                    // 如果今年生日已过，计算明年
                    if (thisYearBirthday.isBefore(today)) {
                        thisYearBirthday = thisYearBirthday.plusYears(1);
                    }
                    long daysUntil = ChronoUnit.DAYS.between(today, thisYearBirthday);

                    DashboardVO.BirthdayReminder reminder = new DashboardVO.BirthdayReminder();
                    reminder.setCtId(c.getCtId());
                    reminder.setCtName(c.getCtName());
                    reminder.setCtBirth(birth);
                    reminder.setDaysUntilBirthday(daysUntil);

                    if (daysUntil == 0) {
                        reminder.setDescription("今天生日 🎂");
                    } else if (daysUntil <= 7) {
                        reminder.setDescription(daysUntil + "天后生日");
                    } else {
                        reminder.setDescription(daysUntil + "天后生日");
                    }

                    return reminder;
                })
                // 按距离生日天数排序（最近的在前面）
                .sorted(Comparator.comparingLong(DashboardVO.BirthdayReminder::getDaysUntilBirthday))
                // 取前7条
                .limit(7)
                .collect(Collectors.toList());

        return reminders;
    }
}