package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gasstation.entity.FuelOrder;
import com.gasstation.mapper.FuelOrderMapper;
import com.gasstation.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台首页统计服务
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StationService stationService;
    private final MemberService memberService;
    private final FuelOrderService fuelOrderService;
    private final FuelOrderMapper fuelOrderMapper;

    public DashboardVO summary() {
        DashboardVO vo = new DashboardVO();
        vo.setStationCount(stationService.count());
        vo.setMemberCount(memberService.count());
        vo.setUnpaidOrderCount(fuelOrderService.count(new LambdaQueryWrapper<FuelOrder>()
                .eq(FuelOrder::getStatus, FuelOrder.STATUS_UNPAID)));

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        List<FuelOrder> todayOrders = fuelOrderService.list(new LambdaQueryWrapper<FuelOrder>()
                .ge(FuelOrder::getPayTime, startOfDay)
                .in(FuelOrder::getStatus, List.of(FuelOrder.STATUS_PAID, FuelOrder.STATUS_FINISHED)));
        vo.setTodayOrderCount((long) todayOrders.size());
        BigDecimal todaySales = todayOrders.stream()
                .map(order -> order.getPayAmount() == null ? BigDecimal.ZERO : order.getPayAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTodaySales(todaySales.setScale(2, java.math.RoundingMode.HALF_UP));
        vo.setDailyStats(buildDailyStats());
        return vo;
    }

    /** 补齐最近 7 天的销售趋势 */
    private List<DashboardVO.DailyStat> buildDailyStats() {
        Map<String, DashboardVO.DailyStat> statMap = new HashMap<>();
        for (DashboardVO.DailyStat stat : fuelOrderMapper.selectDailyStats(LocalDate.now().minusDays(6).toString())) {
            statMap.put(stat.getDay(), stat);
        }
        List<DashboardVO.DailyStat> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String day = LocalDate.now().minusDays(i).toString();
            DashboardVO.DailyStat stat = statMap.get(day);
            if (stat == null) {
                stat = new DashboardVO.DailyStat();
                stat.setDay(day);
                stat.setOrderCount(0L);
                stat.setAmount(BigDecimal.ZERO);
            }
            result.add(stat);
        }
        return result;
    }
}
