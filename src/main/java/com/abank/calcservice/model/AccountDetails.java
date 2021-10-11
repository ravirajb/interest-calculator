package com.abank.calcservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "account_details")
@Data
public class AccountDetails {
    @Id
    @JsonProperty("identification")
    private Long accountId;
    @JsonProperty("bsb")
    private String bsb;
    @JsonProperty("openingDate")
    private Timestamp createDate;
}
