package com.abank.calcservice.scheduler;

import com.abank.calcservice.handler.AccountHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MonthlyAccountSummary {

    @Autowired
    private AccountHandler accountHandler;

    @Scheduled(cron = "@monthly")
    public void monthlyAccrual() {
        accountHandler.publishMonthlyAccruals();
    }
}
