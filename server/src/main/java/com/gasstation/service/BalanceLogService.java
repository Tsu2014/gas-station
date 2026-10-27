package com.gasstation.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gasstation.entity.BalanceLog;
import com.gasstation.mapper.BalanceLogMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 余额流水服务
 */
@Service
public class BalanceLogService extends ServiceImpl<BalanceLogMapper, BalanceLog> {

    public void record(Long memberId, Integer type, BigDecimal amount, BigDecimal balanceAfter, String bizNo, String remark) {
        BalanceLog log = new BalanceLog();
        log.setMemberId(memberId);
        log.setType(type);
        log.setAmount(amount);
        log.setBalanceAfter(balanceAfter);
        log.setBizNo(bizNo);
        log.setRemark(remark);
        save(log);
    }
}
