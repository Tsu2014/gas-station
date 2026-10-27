package com.gasstation.vo;

import com.gasstation.entity.FuelPrice;
import com.gasstation.entity.Gun;
import com.gasstation.entity.Station;
import lombok.Data;

import java.util.List;

/**
 * 油站详情（含油价与油枪）
 */
@Data
public class StationVO {

    private Station station;

    private List<FuelPrice> prices;

    private List<Gun> guns;
}
