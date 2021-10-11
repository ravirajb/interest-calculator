package com.abank.calcservice.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EODPayload {
    private Date balanceDate;
    private List<AccountBalance> balanceList;
}
