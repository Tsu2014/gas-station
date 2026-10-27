package com.gasstation.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 油品类型
 */
@Getter
@AllArgsConstructor
public enum FuelType {

    G92("92#", "92号汽油"),
    G95("95#", "95号汽油"),
    G98("98#", "98号汽油"),
    D0("0#", "0号柴油");

    private final String code;
    private final String label;

    public static FuelType of(String code) {
        for (FuelType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new BusinessException("不支持的油品类型：" + code);
    }
}
