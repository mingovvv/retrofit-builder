package com.example.retrofitbuilder.config.builder;

import okhttp3.OkHttpClient;

public interface OkHttpClientBuilderCustomizer {
    void customize(OkHttpClient.Builder okHttpClientBuilder);
}
