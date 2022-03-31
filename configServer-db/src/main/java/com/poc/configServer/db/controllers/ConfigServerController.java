package com.poc.configServer.db.controllers;

import com.netflix.discovery.converters.Auto;
import com.poc.configServer.db.entity.DbPropertie;
import com.poc.configServer.db.hilos.AsyncRefreshMicroservices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.poc.configServer.db.constants.StringConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ConfigServerController {
    private Logger log = LoggerFactory.getLogger(ConfigServerController.class);

    @Auto
    Environment env;
    @PostMapping("/{canal}")
    public ResponseEntity<?> pruebaPropagarRefresh(@PathVariable String canal, @RequestBody DbPropertie newPropertie){
        if(true){
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> configServerRequest = new HttpEntity<String>(headers);
            HttpEntity<String> eurekaRequest = new HttpEntity<String>(headers);
            //se refresca el config server
            ResponseEntity<String> configServerResponse = (ResponseEntity<String>) makeRequest(configServerRequest,
                    "http://127.0.0.1:"+env.getProperty("server.port")+"/actuator/refresh",
                    String.class);

            //llamar a pedir los endpoints del eureka
            ResponseEntity<List<String>> instanciasToUpdate = (ResponseEntity<List<String>>) makeRequest(eurekaRequest, StringConstants.EUREKA_INSTANCIAS_ENDPOINT.concat("canal"),
                    String.class);

            //si se actualizo el config server entonces se llamaria a actualizar las instancias.
            if(confirmResponse(configServerResponse) && confirmResponse(instanciasToUpdate)){
                actualizarInstancias(instanciasToUpdate.getBody());
            }
        }
        return new ResponseEntity<Map<String, Object>>(AsyncRefreshMicroservices.REFRESH_MICRO_RESPONSES.get(), HttpStatus.OK);
    }
    @PostMapping("/{canal}")
    public ResponseEntity<?> agregarPropiedad(@PathVariable String canal, @RequestBody DbPropertie newPropertie){
        Map<String, Object> res = new HashMap<>(); //body response
        HttpEntity<DbPropertie> dbRequest = new HttpEntity<>(newPropertie);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DbPropertie> crudResponse
                = (ResponseEntity<DbPropertie>) makeRequest(dbRequest, StringConstants.CRUD_POST_PROPERTIE_ENDPOINT,  DbPropertie.class);

        //confirmamos si el crud se realizo
        if(confirmResponse(crudResponse)){
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> configServerRequest = new HttpEntity<String>(headers);
            HttpEntity<String> eurekaRequest = new HttpEntity<String>(headers);
            //se refresca el config server
            ResponseEntity<String> configServerResponse = (ResponseEntity<String>) makeRequest(configServerRequest,
                    "http://127.0.0.1:"+env.getProperty("server.port")+"/actuator/refresh",
                    String.class);

            //llamar a pedir los endpoints del eureka
            ResponseEntity<List<String>> instanciasToUpdate = (ResponseEntity<List<String>>) makeRequest(eurekaRequest, StringConstants.EUREKA_INSTANCIAS_ENDPOINT.concat("canal"),
                    String.class);

            //si se actualizo el config server entonces se llamaria a actualizar las instancias.
            if(confirmResponse(configServerResponse) && confirmResponse(instanciasToUpdate)){
                actualizarInstancias(instanciasToUpdate.getBody());
            }
        }

        return new ResponseEntity<Map<String, Object>>(AsyncRefreshMicroservices.REFRESH_MICRO_RESPONSES.get(), HttpStatus.OK);
    }

    private boolean confirmResponse(ResponseEntity<?> res){
        if(res.getStatusCode().equals(HttpStatus.OK)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private ResponseEntity<?> makeRequest(HttpEntity<?> body, String url, Class<?> classType){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<?> response = restTemplate.postForEntity(url,body, classType);
        return response;
    }

    private void actualizarInstancias(List<String> instanciasToUpdate){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        for (String baseUrl: instanciasToUpdate) {
            AsyncRefreshMicroservices newAsyncRefreshRequest=new AsyncRefreshMicroservices(restTemplate, headers, baseUrl);
            Thread newThread=new Thread(newAsyncRefreshRequest);
            newThread.start();
        }


    }

}
