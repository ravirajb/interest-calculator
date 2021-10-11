package com.abank.calcservice.model;

import lombok.Data;

@Data
public class AccountBalance {
    private String bsb;
    private Long identification;
    private Double balance;
    private String accountCcy = "AUD";
}
