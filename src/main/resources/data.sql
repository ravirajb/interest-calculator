DROP TABLE IF EXISTS account_details;

CREATE TABLE account_details (
  account_id number PRIMARY KEY,
  create_date date not null,
  bsb VARCHAR(6) not null
);

insert into ACCOUNT_DETAILS
values (111222333, sysdate, 182182);

insert into ACCOUNT_DETAILS
values (222000999, sysdate, 182182);

insert into ACCOUNT_DETAILS
values (222000111, sysdate, 182182);

DROP TABLE IF EXISTS account_accruals;

CREATE TABLE account_accruals (
    account_id number not null,
    bsb VARCHAR(6) not null,
    balance_date date not null,
    balance decimal(10,2)  default 0.0,
    accrual_amount decimal(10,2) default 0.0,
    final_balance decimal(10,2) default 0.0
);