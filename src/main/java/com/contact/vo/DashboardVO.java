package com.contact.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Dashboard仪表盘数据VO
 *
 * @author Contact Manager
 */
@Data
@Schema(description = "Dashboard数据")
public class DashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计卡片
     */
    @Schema(description = "统计卡片")
    private StatsCard statsCard;

    /**
     * 性别分布
     */
    @Schema(description = "性别分布")
    private List<ChartItem> genderDistribution;

    /**
     * 事项完成率
     */
    @Schema(description = "事项完成率")
    private CompletionRate matterCompletion;

    /**
     * 联系人增长趋势（最近6个月）
     */
    @Schema(description = "联系人增长趋势")
    private List<GrowthItem> contactGrowth;

    /**
     * 最近生日提醒
     */
    @Schema(description = "最近生日提醒")
    private List<BirthdayReminder> birthdayReminders;

    @Data
    @Schema(description = "统计卡片")
    public static class StatsCard implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "联系人总数")
        private Long totalContacts;

        @Schema(description = "黑名单人数")
        private Long blacklistCount;

        @Schema(description = "待完成事项数")
        private Long pendingMatters;

        @Schema(description = "已完成事项数")
        private Long completedMatters;

        @Schema(description = "本月生日人数")
        private Long birthdayThisMonth;
    }

    @Data
    @Schema(description = "图表项目")
    public static class ChartItem implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "数值")
        private Long value;

        public ChartItem() {}

        public ChartItem(String name, Long value) {
            this.name = name;
            this.value = value;
        }
    }

    @Data
    @Schema(description = "事项完成率")
    public static class CompletionRate implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "已完成数量")
        private Long completed;

        @Schema(description = "未完成数量（待完成+已取消）")
        private Long uncompleted;

        @Schema(description = "完成百分比")
        private Double percentage;
    }

    @Data
    @Schema(description = "增长趋势项目")
    public static class GrowthItem implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "月份（yyyy-MM）")
        private String month;

        @Schema(description = "新增数量")
        private Long count;

        public GrowthItem() {}

        public GrowthItem(String month, Long count) {
            this.month = month;
            this.count = count;
        }
    }

    @Data
    @Schema(description = "生日提醒")
    public static class BirthdayReminder implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "联系人ID")
        private String ctId;

        @Schema(description = "联系人姓名")
        private String ctName;

        @Schema(description = "出生日期")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate ctBirth;

        @Schema(description = "距离生日天数（0表示今天）")
        private Long daysUntilBirthday;

        @Schema(description = "生日描述（今天生日/X天后生日）")
        private String description;
    }
}