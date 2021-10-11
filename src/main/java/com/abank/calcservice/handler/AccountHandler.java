package com.abank.calcservice.handler;

import com.abank.calcservice.dao.AccountDAO;
import com.abank.calcservice.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AccountHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AccountRepository accountRepository;

    @Value("spring.kafka.missingAccountTopic")
    private String errorTopic;

    @Value("spring.kafka.outputTopic")
    private String outputTopic;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    public Mono<ServerResponse> saveAccount(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(AccountDetails.class)
                .flatMap(accountDetails -> {
                    log.info("saving accountDetails:{}", accountDetails);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Mono.just(accountRepository.save(accountDetails)),
                                    AccountDetails.class)
                            .doOnError(Mono::just)
                            .switchIfEmpty(ServerResponse.badRequest().build());
                });
    }

    public void accrueForAccounts(final EODPayload eodPayload) throws JsonProcessingException {
        Date balanceDate = eodPayload.getBalanceDate();
        final Date finalDate = DateUtils.truncate(balanceDate, Calendar.DATE);

        List<AccountAccrual> accrualList = new ArrayList<>();
        List<String> missingAccounts = new ArrayList<>();

        eodPayload.getBalanceList()
                .forEach(rec -> {
                    boolean isAccountExists = accountRepository.existsById(rec.getIdentification());
                    log.info("id: {}, isAccountExists: {}",
                            rec.getIdentification(), isAccountExists);
                    if (!isAccountExists) {
                        missingAccounts.add(String.valueOf(rec.getIdentification()));
                    }

                    if (isAccountExists && rec.getBalance() > 0) {
                        double accruedAmount = rec.getBalance() * 0.01;
                        accrualList.add(AccountAccrual.builder()
                                .accountId(rec.getIdentification())
                                .bsb(rec.getBsb())
                                .balanceDate(new Timestamp(finalDate.getTime()))
                                .balance(rec.getBalance())
                                .accrualAmount(accruedAmount)
                                .final_balance(rec.getBalance() + accruedAmount)
                                .build());
                    }
                });

        accountDAO.maintainAccruals(accrualList);
        kafkaTemplate.send(errorTopic, mapper.writeValueAsString(MissingAccounts.builder()
                .missingAccounts(missingAccounts)
                .build()));
    }

    public void publishMonthlyAccruals() {
        accountDAO.getMontlyAccrual()
                .flatMap(monthlyData -> {
                    try {
                        kafkaTemplate.send(outputTopic, mapper.writeValueAsString(monthlyData));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return Flux.empty();
                });
    }
}
