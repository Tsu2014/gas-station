package com.gasstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gasstation.entity.MemberCoupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会员优惠券 Mapper
 */
public interface MemberCouponMapper extends BaseMapper<MemberCoupon> {

    /**
     * 查询会员可用券（联表带出券模板信息），按优惠力度倒序
     */
    @Select("SELECT mc.*, c.name AS coupon_name, c.type AS coupon_type, c.threshold, c.amount, " +
            "c.discount AS coupon_discount, c.end_time FROM gs_member_coupon mc " +
            "JOIN gs_coupon c ON mc.coupon_id = c.id AND c.deleted = 0 " +
            "WHERE mc.deleted = 0 AND mc.member_id = #{memberId} AND mc.status = #{status} " +
            "AND c.end_time >= NOW() ORDER BY c.amount DESC")
    List<MemberCoupon> selectAvailable(@Param("memberId") Long memberId, @Param("status") Integer status);
}
