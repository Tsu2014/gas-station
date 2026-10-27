package com.gasstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gasstation.entity.Station;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 加油站 Mapper
 */
public interface StationMapper extends BaseMapper<Station> {

    /**
     * 按经纬度查询附近油站（距离单位：公里）
     */
    @Select("SELECT s.*, ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - s.lat * PI() / 180) / 2), 2) " +
            "+ COS(#{lat} * PI() / 180) * COS(s.lat * PI() / 180) * POW(SIN((#{lng} * PI() / 180 - s.lng * PI() / 180) / 2), 2))), 2) AS distance " +
            "FROM gs_station s WHERE s.deleted = 0 AND s.status = 1 ORDER BY distance ASC")
    List<Station> selectNearby(@Param("lng") BigDecimal lng, @Param("lat") BigDecimal lat);
}
