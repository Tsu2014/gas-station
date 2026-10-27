package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.common.FuelType;
import com.gasstation.entity.FuelPrice;
import com.gasstation.mapper.FuelPriceMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 油价服务
 */
@Service
public class FuelPriceService extends ServiceImpl<FuelPriceMapper, FuelPrice> {

    /** 查询油站当前生效油价 */
    public List<FuelPrice> listByStation(Long stationId) {
        return list(new LambdaQueryWrapper<FuelPrice>()
                .eq(FuelPrice::getStationId, stationId)
                .eq(FuelPrice::getStatus, 1)
                .orderByAsc(FuelPrice::getFuelType));
    }

    /** 获取指定油品单价 */
    public BigDecimal getUnitPrice(Long stationId, String fuelType) {
        FuelType.of(fuelType);
        FuelPrice price = getOne(new LambdaQueryWrapper<FuelPrice>()
                .eq(FuelPrice::getStationId, stationId)
                .eq(FuelPrice::getFuelType, fuelType)
                .eq(FuelPrice::getStatus, 1)
                .last("LIMIT 1"));
        if (price == null || price.getSalePrice() == null) {
            throw new BusinessException("该油品暂未定价：" + fuelType);
        }
        return price.getSalePrice();
    }

    /** 新增 / 修改 */
    public void saveOrUpdatePrice(FuelPrice price) {
        FuelType.of(price.getFuelType());
        if (price.getId() == null) {
            if (price.getStatus() == null) {
                price.setStatus(1);
            }
            if (price.getSalePrice() == null) {
                price.setSalePrice(price.getListPrice());
            }
            if (price.getEffectiveTime() == null) {
                price.setEffectiveTime(java.time.LocalDateTime.now());
            }
            save(price);
        } else {
            updateById(price);
        }
    }
}
