package com.example.retrofitbuilder.config.http;


import com.example.retrofitbuilder.config.builder.Retry;
import com.example.retrofitbuilder.dto.request.AAARequest;
import com.example.retrofitbuilder.dto.response.AAAResponse;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface HttpServiceAAA {


    @Retry(maxTryCount = 2)
    @POST("/api/aaa")
    @Headers("Content-Type: application/json")
    AAAResponse postOrderStatusAddone(@Body AAARequest body);

}
