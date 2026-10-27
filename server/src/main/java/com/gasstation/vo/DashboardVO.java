package com.gasstation.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 后台首页统计数据
 */
@Data
public class DashboardVO {

    /** 油站数 */
    private Long stationCount;

    /** 会员数 */
    private Long memberCount;

    /** 今日订单数 */
    private Long todayOrderCount;

    /** 今日销售额 */
    private BigDecimal todaySales;

    /** 待支付订单数 */
    private Long unpaidOrderCount;

    /** 近 7 日销售趋势 */
    private List<DailyStat> dailyStats;

    @Data
    public static class DailyStat {
        private String day;
        private Long orderCount;
        private BigDecimal amount;
    }
}
