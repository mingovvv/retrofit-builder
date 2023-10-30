package com.example.retrofitbuilder.config.builder.converterfactories;

import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link LocalTime}
 */
public class LocalTimeParamConverterFactory extends Converter.Factory {
    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        // 관련 없는 Type 은 null 반환
        if (type != LocalTime.class) {
            return null;
        }

        return (Converter<LocalTime, String>) value -> {
            // 사실은 안해도 된다. 기본이 ISO_LOCAL_TIME 이라서. HH:mm:ss
            return value.format(DateTimeFormatter.ISO_LOCAL_TIME);
        };
    }
}
