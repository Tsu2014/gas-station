package com.gasstation.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询基类
 */
@Data
public class PageQuery {

    @Min(value = 1, message = "页码最小为 1")
    private long current = 1;

    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 200, message = "每页条数最大为 200")
    private long size = 10;

    public <T> Page<T> toPage() {
        return new Page<>(current, size);
    }
}
