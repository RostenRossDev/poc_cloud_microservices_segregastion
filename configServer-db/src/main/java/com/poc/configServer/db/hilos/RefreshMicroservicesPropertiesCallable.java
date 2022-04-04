package com.poc.configServer.db.hilos;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class RefreshMicroservicesPropertiesCallable implements Callable<String> {
	private Logger log = LoggerFactory.getLogger(RefreshMicroservicesPropertiesCallable.class);

    private RestTemplate restTemplate;
    private HttpEntity<String> request;
    private String baseUrl;
    
    public RefreshMicroservicesPropertiesCallable(RestTemplate restTemplate, HttpEntity<String> request, String baseUrl) {
        this.restTemplate = restTemplate;
        this.request = request;
        this.baseUrl = baseUrl;
    }

    @Override
	public String call() throws Exception {
    	log.info(request.toString());
        String endpoint = this.baseUrl.concat("/actuator/refresh");
        ResponseEntity<?> response = restTemplate.postForEntity(endpoint,request, Object.class);
        log.info("Endpoint refrescado: "+this.baseUrl.concat("actuator/refresh").toString());
        
        if(response.getStatusCode().equals(HttpStatus.OK)) {
    		return this.baseUrl.concat("actuator/refresh").toString();
        }
        //chequear que pasa si la llamada es negativa
        return null;
    }	
}
