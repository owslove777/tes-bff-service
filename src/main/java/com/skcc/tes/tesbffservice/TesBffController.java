package com.skcc.tes.tesbffservice;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


@RestController
@RequiredArgsConstructor
public class TesBffController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate restTemplate;

    @Autowired
    CircuitBreakerFactory circuitBreakerFactory;

    @Value("${api.sample.url}")
    private String apiGatewayUrl;


    @GetMapping("/health_check")
    public String status() {

        log.info("==================health_check===================================");
        return "It's Working in tes_bff Service";
    }

    @GetMapping("/circuit")
    public Tes tes_circuit() {

        log.info("==================circuit  start===================================");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Tes tes = circuitBreaker.run(() -> restTemplate.getForObject(String.format("%s%s",apiGatewayUrl,"/tesgood"), Tes.class)
                ,throwable -> new Tes());

        log.info("getBody: {}", tes.getName());
        log.info("==================circuit  end===================================");
        return tes ;
    }


    @GetMapping("/circuit/timeout")
    public Tes tes_circuitTimeout() {

        log.info("==================circuit timeout start===================================");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Tes tes = circuitBreaker.run(() -> restTemplate.getForObject(String.format("%s%s",apiGatewayUrl,"/tesbad"), Tes.class)
                ,throwable -> new Tes());

        log.info("getBody: {}", tes.getName());
        log.info("==================circuit timeout end===================================");
        return tes ;
    }

    @GetMapping("/circuit/no")
    public Tes tes_circuitNo() {

        log.info("==================circuit no start===================================");
        Tes tes = restTemplate.getForObject(String.format("%s%s",apiGatewayUrl,"/tesbad"), Tes.class);

        log.info("getBody: {}", tes.getName());
        log.info("==================circuit no end===================================");
        return tes ;
    }
}
