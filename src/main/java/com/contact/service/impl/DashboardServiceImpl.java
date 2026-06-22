package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contact.common.constant.SystemConstants;
import com.contact.common.utils.UserContext;
import com.contact.entity.Contact;
import com.contact.entity.ContactTagRel;
import com.contact.entity.Matter;
import com.contact.mapper.ContactMapper;
import com.contact.mapper.ContactTagRelMapper;
import com.contact.mapper.MatterMapper;
import com.contact.service.DashboardService;
import com.contact.vo.DashboardStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ContactMapper contactMapper;
    private final MatterMapper matterMapper;
    private final ContactTagRelMapper contactTagRelMapper;

    @Override
    public DashboardStatsVO getDashboardStats() {
        String userId = UserContext.getUserId();
        log.info("获取Dashboard统计数据: userId={}", userId);

        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setStatsCard(buildStatsCard(userId));
        vo.setGenderDistribution(buildGenderDistribution(userId));
        vo.setMatterCompletion(buildCompletionRate(userId));
        vo.setContactGrowth(buildContactGrowth(userId));
        vo.setBirthdayReminders(buildBirthdayReminders(userId));
        return vo;
    }

    /**
     * 统计卡片
     */
    private DashboardStatsVO.StatsCard buildStatsCard(String userId) {
        DashboardStatsVO.StatsCard card = new DashboardStatsVO.StatsCard();

        // 联系人总数
        card.setTotalContacts(countContacts(userId, SystemConstants.CONTACT_STATUS_NORMAL));

        // 黑名单人数
        card.setBlacklistCount(countContacts(userId, SystemConstants.CONTACT_STATUS_BLACKLIST));

        // 待完成事项
        LambdaQueryWrapper<Matter> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Matter::getUserId, userId)
                    .eq(Matter::getMatterDelete, SystemConstants.MATTER_STATUS_PENDING);
        card.setPendingMatters(Math.toIntExact(matterMapper.selectCount(pendingQuery)));

        // 已完成事项
        LambdaQueryWrapper<Matter> completedQuery = new LambdaQueryWrapper<>();
        completedQuery.eq(Matter::getUserId, userId)
                      .eq(Matter::getMatterDelete, SystemConstants.MATTER_STATUS_COMPLETED);
        card.setCompletedMatters(Math.toIntExact(matterMapper.selectCount(completedQuery)));

        // 本月生日人数
        int currentMonth = LocalDate.now().getMonthValue();
        LambdaQueryWrapper<Contact> birthdayQuery = new LambdaQueryWrapper<>();
        birthdayQuery.eq(Contact::getUserId, userId)
                     .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL)
                     .isNotNull(Contact::getCtBirth);
        List<Contact> allContacts = contactMapper.selectList(birthdayQuery);
        long birthdayCount = allContacts.stream()
                .filter(c -> c.getCtBirth() != null && c.getCtBirth().getMonthValue() == currentMonth)
                .count();
        card.setBirthdayThisMonth(Math.toIntExact(birthdayCount));

        return card;
    }

    /**
     * 性别分布
     */
    private List<DashboardStatsVO.GenderItem> buildGenderDistribution(String userId) {
        LambdaQueryWrapper<Contact> maleQuery = new LambdaQueryWrapper<>();
        maleQuery.eq(Contact::getUserId, userId)
                 .eq(Contact::getCtMf, "男")
                 .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL);
        long maleCount = contactMapper.selectCount(maleQuery);

        LambdaQueryWrapper<Contact> femaleQuery = new LambdaQueryWrapper<>();
        femaleQuery.eq(Contact::getUserId, userId)
                   .eq(Contact::getCtMf, "女")
                   .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL);
        long femaleCount = contactMapper.selectCount(femaleQuery);

        List<DashboardStatsVO.GenderItem> result = new ArrayList<>();
        result.add(new DashboardStatsVO.GenderItem("男", maleCount));
        result.add(new DashboardStatsVO.GenderItem("女", femaleCount));
        return result;
    }

    /**
     * 事项完成率
     */
    private DashboardStatsVO.CompletionRate buildCompletionRate(String userId) {
        LambdaQueryWrapper<Matter> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Matter::getUserId, userId)
                    .eq(Matter::getMatterDelete, SystemConstants.MATTER_STATUS_PENDING);
        long pendingCount = matterMapper.selectCount(pendingQuery);

        LambdaQueryWrapper<Matter> completedQuery = new LambdaQueryWrapper<>();
        completedQuery.eq(Matter::getUserId, userId)
                      .eq(Matter::getMatterDelete, SystemConstants.MATTER_STATUS_COMPLETED);
        long completedCount = matterMapper.selectCount(completedQuery);

        return new DashboardStatsVO.CompletionRate(
                Math.toIntExact(completedCount), 
                Math.toIntExact(pendingCount)
        );
    }

    /**
     * 联系人增长趋势（最近6个月）
     */
    private List<DashboardStatsVO.GrowthItem> buildContactGrowth(String userId) {
        List<DashboardStatsVO.GrowthItem> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        LambdaQueryWrapper<Contact> query = new LambdaQueryWrapper<>();
        LocalDateTime sixMonthsAgo = now.minusMonths(5).withDayOfMonth(1).atStartOfDay();
        query.eq(Contact::getUserId, userId)
             .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL)
             .ge(Contact::getCreatedAt, sixMonthsAgo)
             .orderByAsc(Contact::getCreatedAt);

        List<Contact> contacts = contactMapper.selectList(query);

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i).withDayOfMonth(1);
            String monthKey = monthDate.format(formatter);
            LocalDateTime monthStart = monthDate.atStartOfDay();
            LocalDateTime monthEnd = monthDate.plusMonths(1).atStartOfDay();

            long count = contacts.stream()
                    .filter(c -> c.getCreatedAt() != null
                            && !c.getCreatedAt().isBefore(monthStart)
                            && c.getCreatedAt().isBefore(monthEnd))
                    .count();

            result.add(new DashboardStatsVO.GrowthItem(monthKey, count));
        }

        return result;
    }

    /**
     * 生日提醒（最近30天内）
     */
    private List<DashboardStatsVO.BirthdayReminder> buildBirthdayReminders(String userId) {
        List<DashboardStatsVO.BirthdayReminder> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<Contact> query = new LambdaQueryWrapper<>();
        query.eq(Contact::getUserId, userId)
             .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL)
             .isNotNull(Contact::getCtBirth);

        List<Contact> contacts = contactMapper.selectList(query);

        for (Contact contact : contacts) {
            if (contact.getCtBirth() == null) continue;

            LocalDate birthDate = contact.getCtBirth();
            LocalDate thisYearBirthday = birthDate.withYear(today.getYear());

            if (thisYearBirthday.isBefore(today)) {
                thisYearBirthday = thisYearBirthday.plusYears(1);
            }

            long daysUntil = ChronoUnit.DAYS.between(today, thisYearBirthday);

            if (daysUntil >= 0 && daysUntil <= 30) {
                result.add(new DashboardStatsVO.BirthdayReminder(
                        contact.getCtId(),
                        contact.getCtName(),
                        contact.getCtBirth().toString(),
                        (int) daysUntil
                ));
            }
        }

        result.sort(Comparator.comparingInt(DashboardStatsVO.BirthdayReminder::getDaysUntilBirthday));
        return result;
    }

    private long countContacts(String userId, Integer status) {
        LambdaQueryWrapper<Contact> query = new LambdaQueryWrapper<>();
        query.eq(Contact::getUserId, userId)
             .eq(Contact::getCtDelete, status);
        return contactMapper.selectCount(query);
    }
}
