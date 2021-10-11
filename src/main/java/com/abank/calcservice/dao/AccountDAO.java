package com.abank.calcservice.dao;

import com.abank.calcservice.model.AccountAccrual;
import com.abank.calcservice.model.MonthlyData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AccountDAO {

    /*String ACCRUE_FOR_ACCOUNT =
            "MERGE INTO account_accruals AS T USING " +
                    "(SELECT * FROM account_accruals where balance_date = :balance_date)  AS S\n" +
                    "    ON T.balance_date = S.balance_date \n" +
                    "        and T.balance_date = S.balance_date \n" +
                    "    WHEN MATCHED THEN\n" +
                    "        UPDATE SET T.balance = :balance,\n" +
                    "            T.accrual_amount = :accrual_amount,\n" +
                    "            T.final_balance = :final_balance\n" +
                    "    WHEN NOT MATCHED THEN\n" +
                    "        INSERT (account_id, bsb, balance_date, balance, accrual_amount, final_balance) \n" +
                    "        VALUES (:account_id, :bsb, :balance_date, :balance, :accrual_amount, :final_balance) ";*/

    String MAINTAIN_ACCRUALS = "INSERT into account_accruals (account_id, bsb, balance_date, balance, accrual_amount, final_balance) " +
            "VALUES (:account_id, :bsb, :balance_date, :balance, :accrual_amount, :final_balance) ";

    String UPDATE_ACCOUNT_ACCRUAL = "Update account_accruals " +
            "       set  balance = :balance,\n" +
            "            accrual_amount = :accrual_amount,\n" +
            "            final_balance = :final_balance\n " +
            "where balance_date = :balance_date and account_id = :account_id";

    String IS_ACCRUAL_DONE_FOR_TODAY = "select count(1) from account_accruals " +
            "where balance_date = :balance_date and account_id = :account_id ";

    String MONTHLY_ACCRUALS = "select * \n" +
            "from \n" +
            "(select account_id,\n" +
            "  final_balance, \n" +
            "  balance_date,\n" +
            "  row_number() over(partition by account_id order by balance_date desc ) as rn \n" +
            "from ACCOUNT_ACCRUALS \n" +
            "where balance_date > sysdate - 60\n" +
            ") abc \n" +
            "where rn = 1 \n";


    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void maintainAccruals(final List<AccountAccrual> accountAccrualList) {

        for (AccountAccrual sb : accountAccrualList) {
            Map<String, Object> map = new HashMap<>();
            map.put("account_id", sb.getAccountId());
            map.put("bsb", sb.getBsb());
            map.put("balance_date", sb.getBalanceDate());
            map.put("balance", sb.getBalance());
            map.put("accrual_amount", sb.getAccrualAmount());
            map.put("final_balance", sb.getFinal_balance());

            int isAccrualDone = namedParameterJdbcTemplate.queryForObject(IS_ACCRUAL_DONE_FOR_TODAY,
                    map, Integer.class);

            if (isAccrualDone > 0) {
                namedParameterJdbcTemplate.update(UPDATE_ACCOUNT_ACCRUAL, map);
            } else {
                namedParameterJdbcTemplate.update(MAINTAIN_ACCRUALS, map);
            }
        }
    }

    public Flux<MonthlyData> getMontlyAccrual() {
        return Mono.fromCallable(() ->
                namedParameterJdbcTemplate.query(MONTHLY_ACCRUALS, new HashMap<>(),
                        BeanPropertyRowMapper.newInstance(MonthlyData.class)
                ))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);

    }
}
