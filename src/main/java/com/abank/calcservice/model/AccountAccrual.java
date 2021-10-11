package com.abank.calcservice.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class AccountAccrual {
    private Long accountId;
    private String bsb;
    private Timestamp balanceDate;
    private Double balance;
    private Double accrualAmount;
    private Double final_balance;
}
