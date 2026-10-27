package com.gasstation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.common.BusinessException;
import com.gasstation.common.PageResult;
import com.gasstation.dto.StationQueryDTO;
import com.gasstation.entity.FuelPrice;
import com.gasstation.entity.Gun;
import com.gasstation.entity.Station;
import com.gasstation.mapper.StationMapper;
import com.gasstation.vo.StationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 油站服务
 */
@Service
@RequiredArgsConstructor
public class StationService extends ServiceImpl<StationMapper, Station> {

    private final FuelPriceService fuelPriceService;
    private final GunService gunService;

    /** 后台分页查询 */
    public PageResult<Station> pageQuery(StationQueryDTO query) {
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<Station>()
                .like(StringUtils.hasText(query.getKeyword()), Station::getName, query.getKeyword())
                .eq(query.getStatus() != null, Station::getStatus, query.getStatus())
                .orderByDesc(Station::getId);
        return PageResult.of(page(query.toPage(), wrapper));
    }

    /** 附近油站（按距离升序） */
    public List<Station> nearby(BigDecimal lng, BigDecimal lat, String keyword) {
        List<Station> list = baseMapper.selectNearby(lng, lat);
        if (StringUtils.hasText(keyword)) {
            list = list.stream()
                    .filter(s -> s.getName() != null && s.getName().contains(keyword))
                    .collect(Collectors.toList());
        }
        return list.size() > 50 ? new ArrayList<>(list.subList(0, 50)) : list;
    }

    /** 油站详情（含油价、油枪） */
    public StationVO detail(Long id) {
        Station station = getById(id);
        if (station == null) {
            throw new BusinessException("油站不存在");
        }
        StationVO vo = new StationVO();
        vo.setStation(station);
        List<FuelPrice> prices = fuelPriceService.listByStation(id);
        vo.setPrices(prices);
        vo.setGuns(gunService.listByStation(id));
        return vo;
    }

    /** 新增 / 修改 */
    public void saveOrUpdateStation(Station station) {
        if (station.getId() == null) {
            if (!StringUtils.hasText(station.getCode())) {
                station.setCode("S" + System.currentTimeMillis());
            }
            if (station.getStatus() == null) {
                station.setStatus(1);
            }
            save(station);
        } else {
            if (getById(station.getId()) == null) {
                throw new BusinessException("油站不存在");
            }
            updateById(station);
        }
    }
}
