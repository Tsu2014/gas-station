package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.entity.Gun;
import com.gasstation.mapper.GunMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 加油枪服务
 */
@Service
public class GunService extends ServiceImpl<GunMapper, Gun> {

    /** 空闲 */
    public static final int STATUS_FREE = 1;
    /** 使用中 */
    public static final int STATUS_BUSY = 2;
    /** 故障 */
    public static final int STATUS_BROKEN = 3;

    public List<Gun> listByStation(Long stationId) {
        return list(new LambdaQueryWrapper<Gun>()
                .eq(Gun::getStationId, stationId)
                .orderByAsc(Gun::getGunNo));
    }

    public Gun checkAvailable(Long gunId, Long stationId) {
        Gun gun = getById(gunId);
        if (gun == null) {
            throw new BusinessException("加油枪不存在");
        }
        if (!gun.getStationId().equals(stationId)) {
            throw new BusinessException("加油枪与油站不匹配");
        }
        if (gun.getStatus() == null || gun.getStatus() != STATUS_FREE) {
            throw new BusinessException("该加油枪当前不可用");
        }
        return gun;
    }

    public void changeStatus(Long gunId, int status) {
        if (gunId == null) {
            return;
        }
        Gun gun = new Gun();
        gun.setId(gunId);
        gun.setStatus(status);
        updateById(gun);
    }
}
