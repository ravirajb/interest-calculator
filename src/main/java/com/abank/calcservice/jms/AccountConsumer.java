package com.abank.calcservice.jms;

import com.abank.calcservice.handler.AccountHandler;
import com.abank.calcservice.model.EODPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class AccountConsumer {

    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private AccountHandler accountHandler;

    @KafkaListener(topics = "#{'${spring.kafka.inputTopic}'.split(',')}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(@Payload final String message) throws JsonProcessingException {
        EODPayload payload = mapper.readValue(message, EODPayload.class);
        accountHandler.accrueForAccounts(payload);
    }
}
