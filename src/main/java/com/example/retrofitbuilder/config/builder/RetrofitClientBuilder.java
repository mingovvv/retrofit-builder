package com.example.retrofitbuilder.config.builder;

import com.example.retrofitbuilder.config.builder.converterfactories.LocalDateParamConverterFactory;
import com.example.retrofitbuilder.config.builder.converterfactories.LocalDateTimeParamConverterFactory;
import com.example.retrofitbuilder.config.builder.converterfactories.LocalTimeParamConverterFactory;
import com.example.retrofitbuilder.config.builder.converterfactories.YearParamConverterFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
public class RetrofitClientBuilder {
    private final Logger okhttpLogger = LoggerFactory.getLogger("okhttp");

    public static final Duration DEFAULT_CONNECTION_TIMEOUT_DURATION = Duration.ofMillis(1000L);
    public static final Duration DEFAULT_READ_WRITE_TIMEOUT_DURATION = Duration.ofMillis(5000L);
    public static final Duration DEFAULT_WRITE_WRITE_TIMEOUT_DURATION = Duration.ofMillis(5000L);

    private String baseUrl;
    private Duration connectionTimeout = DEFAULT_CONNECTION_TIMEOUT_DURATION;
    private Duration readTimeout = DEFAULT_READ_WRITE_TIMEOUT_DURATION;
    private Duration writeTimeout = DEFAULT_WRITE_WRITE_TIMEOUT_DURATION;

    private List<OkHttpClientBuilderCustomizer> okHttpClientBuilderCustomizers = Collections.emptyList();

    public RetrofitClientBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RetrofitClientBuilder connectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public RetrofitClientBuilder readTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RetrofitClientBuilder writeTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public RetrofitClientBuilder okHttpClientBuilderCustomizer(List<OkHttpClientBuilderCustomizer> okHttpClientBuilderCustomizers) {
        this.okHttpClientBuilderCustomizers = okHttpClientBuilderCustomizers;
        return this;
    }

    public <T> T build(Class<T> clientClass) {
        // ObjectMapper 를 외부로 노출시키지 말것.
        // 기존 SpringBoot 기본값을 변경해버리면서 다른 API에 영향을 줄 수 있음.
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .setLocale(Locale.getDefault())
            .setTimeZone(TimeZone.getDefault())
            // 역직렬화 시, 알려지지 않은 필드 무시
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // 역직렬화 시, 알려지지 않은 ENUM 값을 null로 취급
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
            // 시간 관련 객체(LocalDateTime, java.util.Date)를 직렬화 할 때 timestamp 숫자값이 아닌 포맷팅 문자열로 한다.
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
            .connectTimeout(connectionTimeout)
            .readTimeout(readTimeout)
            .writeTimeout(writeTimeout)
            .addInterceptor(new HttpLoggingInterceptor(message -> okhttpLogger.debug("OKHTTP : {}", message)) //okhttp3/logging-interceptor
                .setLevel(HttpLoggingInterceptor.Level.BODY));

        if (okHttpClientBuilderCustomizers != null) {
            for (OkHttpClientBuilderCustomizer okHttpClientBuilderCustomizer : okHttpClientBuilderCustomizers) {
                okHttpClientBuilderCustomizer.customize(okHttpClientBuilder);
            }
        }

        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .addConverterFactory(new LocalDateTimeParamConverterFactory())
            .addConverterFactory(new LocalDateParamConverterFactory())
            .addConverterFactory(new LocalTimeParamConverterFactory())
            .addConverterFactory(new YearParamConverterFactory())
            .client(okHttpClient)
            .build();

        return retrofit.create(clientClass);
    }
}
