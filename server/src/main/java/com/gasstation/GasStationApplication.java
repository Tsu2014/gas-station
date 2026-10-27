package com.gasstation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧加油站平台启动类
 *
 * @author gas-station
 */
@MapperScan("com.gasstation.mapper")
@SpringBootApplication
public class GasStationApplication {

    public static void main(String[] args) {
        SpringApplication.run(GasStationApplication.class, args);
    }
}
