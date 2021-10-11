package com.abank.calcservice.handler;

import com.abank.calcservice.model.AccountDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountHandlerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testAccountCreation() {

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountId(111222333l);
        accountDetails.setBsb("182182");
        accountDetails.setCreateDate(new Timestamp(System.currentTimeMillis()));

        webTestClient
                .post()
                .uri("/save-account")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(accountDetails), AccountDetails.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountDetails.class)
                .value(greeting -> {
                    assertThat(greeting.getAccountId()).isEqualTo(111222333l);
                });
    }
}
