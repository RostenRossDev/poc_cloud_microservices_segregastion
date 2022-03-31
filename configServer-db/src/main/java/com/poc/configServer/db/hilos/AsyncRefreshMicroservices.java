package com.poc.configServer.db.hilos;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AsyncRefreshMicroservices implements Runnable {

    private RestTemplate restTemplate;
    private HttpHeaders reqHeaders;
    private String baseUrl;
    static public final AtomicReference<Map<String, Object>> REFRESH_MICRO_RESPONSES = new AtomicReference<>(Collections.emptyMap());
    static public final AtomicBoolean ALL_MICROSERVICES_REFRESHED = new AtomicBoolean(Boolean.TRUE);
    public AsyncRefreshMicroservices(RestTemplate restTemplate, HttpHeaders reqHeaders, String baseUrl) {
        this.restTemplate = restTemplate;
        this.reqHeaders = reqHeaders;
        this.baseUrl = baseUrl;
    }

    @Override
    public void run() {
        String endpoint = this.baseUrl.concat("/actuator/refresh");
        ResponseEntity<?> response = restTemplate.postForEntity(endpoint,reqHeaders, String.class);

        if(!response.getStatusCode().equals(HttpStatus.OK) && ALL_MICROSERVICES_REFRESHED.equals(Boolean.TRUE)){
            ALL_MICROSERVICES_REFRESHED.set(Boolean.FALSE);
        }
        updateRefreshMicrosResponses(this.baseUrl, response.getStatusCode());
    }

    private void updateRefreshMicrosResponses(String url, HttpStatus status) {

        Map<String, Object> tempList = REFRESH_MICRO_RESPONSES.get();
        tempList.put("baseUrl",url);
        tempList.put("status", status);

        REFRESH_MICRO_RESPONSES.set(tempList);
    }
}
