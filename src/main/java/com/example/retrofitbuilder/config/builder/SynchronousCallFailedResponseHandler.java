package com.example.retrofitbuilder.config.builder;

import retrofit2.Response;

public interface SynchronousCallFailedResponseHandler {

    RuntimeException handleErrorResponse(Response<Object> response);
}
