package com.abank.calcservice.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MonthlyData {
    private Long accountId;
    private Double finalBalance;
    private Timestamp balanceDate;
}
