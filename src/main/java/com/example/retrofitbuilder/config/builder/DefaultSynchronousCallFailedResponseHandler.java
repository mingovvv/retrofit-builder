package com.example.retrofitbuilder.config.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.springframework.util.StringUtils;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
public class DefaultSynchronousCallFailedResponseHandler implements SynchronousCallFailedResponseHandler {
    private final ObjectMapper failedResponseObjectMapper;

    public DefaultSynchronousCallFailedResponseHandler() {
        failedResponseObjectMapper = new ObjectMapper();
    }

    @Override
    public RuntimeException handleErrorResponse(Response<Object> response) {
        int responseStatusCode = response.code();
        ResponseBody errorBody = response.errorBody();
        MediaType errorMediaType = errorBody.contentType();
        String errorBodyString = null;
        try {
            errorBodyString = errorBody.string();

            log.info("failure status code : {}, contentType: {}, mediaType: {}, errorBody : {}",
                    responseStatusCode, errorMediaType, errorMediaType, errorBodyString);

            if (errorMediaType != null && "json".equals(errorMediaType.subtype())) {
                JsonNode jsonNode = failedResponseObjectMapper.reader().readTree(errorBodyString);
                String errorCode = getValue(jsonNode, "errorCode");
                String errorMessage = getValue(jsonNode, "errorMessage");

                // errorCode, errorMessage가 없을 경우
                if (StringUtils.isEmpty(errorCode) || StringUtils.isEmpty(errorMessage)) {
                    return new ApiRequestException(responseStatusCode, "UNKNOWN", errorBodyString);
                }

                return new ApiRequestException(responseStatusCode, errorCode, errorMessage);
            }
            return new ApiRequestException(responseStatusCode, "UNKNOWN", errorBodyString);
        } catch (IOException ex) {
            throw new IllegalStateException("Parsing error response failed. errorBody: " + errorBodyString, ex);
        }
    }

    /**
     * JsonNode에 해당 key가 있을 경우 key return 없을 경우 "" 리턴
     * @param jsonNode
     * @param key
     * @return
     */
    public static String getValue(JsonNode jsonNode, String key) {
        String result = "";

        if (isValid(jsonNode, key)) {
            result = jsonNode.get(key).asText();
        }

        return result;
    }

    /**
     * JsonNode에 해당 key 확인
     * @param jsonNode
     * @param key
     * @return
     */
    public static Boolean isValid(JsonNode jsonNode, String key) {
        Boolean result = true;

        if (StringUtils.isEmpty(jsonNode.get(key))) {
            result = false;
        }

        return result;
    }
}