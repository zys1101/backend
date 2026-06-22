package com.contact.vo;

import lombok.Data;

import java.util.List;

/**
 * Dashboard 统计VO
 */
@Data
public class DashboardStatsVO {

    private StatsCard statsCard;
    private List<GenderItem> genderDistribution;
    private CompletionRate matterCompletion;
    private List<GrowthItem> contactGrowth;
    private List<BirthdayReminder> birthdayReminders;

    @Data
    public static class StatsCard {
        private long totalContacts;
        private long blacklistCount;
        private int pendingMatters;
        private int completedMatters;
        private int birthdayThisMonth;
    }

    @Data
    public static class GenderItem {
        private String name;
        private long value;

        public GenderItem(String name, long value) {
            this.name = name;
            this.value = value;
        }
    }

    @Data
    public static class CompletionRate {
        private int completed;
        private int uncompleted;
        private double percentage;

        public CompletionRate(int completed, int uncompleted) {
            this.completed = completed;
            this.uncompleted = uncompleted;
            int total = completed + uncompleted;
            this.percentage = total > 0 ? Math.round((double) completed / total * 10000) / 100.0 : 0;
        }
    }

    @Data
    public static class GrowthItem {
        private String month;
        private long count;

        public GrowthItem(String month, long count) {
            this.month = month;
            this.count = count;
        }
    }

    @Data
    public static class BirthdayReminder {
        private String ctId;
        private String ctName;
        private String ctBirth;
        private int daysUntilBirthday;
        private String description;

        public BirthdayReminder(String ctId, String ctName, String ctBirth, int daysUntilBirthday) {
            this.ctId = ctId;
            this.ctName = ctName;
            this.ctBirth = ctBirth;
            this.daysUntilBirthday = daysUntilBirthday;
            this.description = buildDescription(daysUntilBirthday);
        }

        private String buildDescription(int days) {
            if (days == 0) return "🎂 今天生日！";
            if (days == 1) return "明天生日";
            return days + "天后生日";
        }
    }
}
