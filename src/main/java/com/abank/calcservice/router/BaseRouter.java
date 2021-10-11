package com.abank.calcservice.router;

import com.abank.calcservice.handler.AccountHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class BaseRouter {
    @Bean
    public RouterFunction<ServerResponse> route(AccountHandler accountHandler) {

        return RouterFunctions
                .route(POST("/save-account")
                        .and(accept(MediaType.APPLICATION_JSON)), accountHandler::saveAccount);
    }
}
