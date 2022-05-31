package com.skcc.tes.tesbffservice;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4) //실패확률, 100번 중 4번 실패하면 CircuitBreaker open , default 50
                .waitDurationInOpenState(Duration.ofMillis(1000)) // CircuitBreaker open 유지시간 , default 60 초
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // CircuitBreaker closed될때 호출결과기록을 시간/횟수기반으로 할지
                .slidingWindowSize(2) // CircuitBreaker closed될때 호출결과기록할 창의 크기
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(3)) //호출된 서비스가 3초간 응답 없을경우 CircuitBreaker open
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build()
        );

    }
}