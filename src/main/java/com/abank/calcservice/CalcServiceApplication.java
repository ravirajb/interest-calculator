package com.abank.calcservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class CalcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalcServiceApplication.class, args);
    }

}
