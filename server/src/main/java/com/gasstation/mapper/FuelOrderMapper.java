package com.gasstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gasstation.entity.FuelOrder;
import com.gasstation.vo.DashboardVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 加油订单 Mapper
 */
public interface FuelOrderMapper extends BaseMapper<FuelOrder> {

    /**
     * 按天统计已支付订单量与销售额
     */
    @Select("SELECT DATE_FORMAT(pay_time, '%Y-%m-%d') AS day, COUNT(*) AS order_count, " +
            "IFNULL(SUM(pay_amount), 0) AS amount FROM gs_order " +
            "WHERE deleted = 0 AND status IN (1, 2) AND pay_time >= #{startDate} " +
            "GROUP BY DATE_FORMAT(pay_time, '%Y-%m-%d') ORDER BY day")
    List<DashboardVO.DailyStat> selectDailyStats(@Param("startDate") String startDate);
}
