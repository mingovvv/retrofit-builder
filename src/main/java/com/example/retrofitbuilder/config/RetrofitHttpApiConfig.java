package com.example.retrofitbuilder.config;

import com.example.retrofitbuilder.config.builder.RetrofitClientBuilder;
import com.example.retrofitbuilder.config.http.HttpServiceAAA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetrofitHttpApiConfig {

    /**
     * AAA service retrofig client builder
     */
    @Bean
    public HttpServiceAAA httpServiceAAA(@Value("${aaa.service.host}") String newOrderBaseUrl,
                                         @Value("${aaa.service.connection-timeout}") Duration connectionTimeout,
                                         @Value("${aaa.service.write-timeout}") Duration writeTimeout,
                                         @Value("${aaa.service.read-timeout}") Duration readTimeout) {
        return new RetrofitClientBuilder()
                .baseUrl(newOrderBaseUrl)
                .connectionTimeout(connectionTimeout)
                .writeTimeout(writeTimeout)
                .readTimeout(readTimeout)
                .build(HttpServiceAAA.class);
    }

}

