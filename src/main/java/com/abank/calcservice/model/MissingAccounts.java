package com.abank.calcservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MissingAccounts {
    private List<String> missingAccounts;
}
