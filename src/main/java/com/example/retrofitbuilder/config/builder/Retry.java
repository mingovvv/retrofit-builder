package com.example.retrofitbuilder.config.builder;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Retry {
    int maxTryCount() default 3;
    long backOffMillis() default 0L;
}
