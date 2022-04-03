package com.poc.configServer.db.controllers;

import com.poc.configServer.db.entity.DbPropertie;
import com.poc.configServer.db.hilos.RefreshMicroservicesPropertiesCallable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.poc.configServer.db.constants.StringConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RefreshScope
@RestController
public class ConfigServerController {
    private Logger log = LoggerFactory.getLogger(ConfigServerController.class);
      
    @Autowired
	private ContextRefresher contextRefresher;

    @SuppressWarnings("unchecked")
	@PostMapping("/{canal}")
    public ResponseEntity<?> propagarRefresh(@PathVariable String canal, @RequestBody DbPropertie newProperty){
    	Map<String, Object> response= new HashMap<>();
        ResponseEntity<Object> instanciasToUpdate;
        RestTemplate restTemplate = new RestTemplate();
        
        HttpEntity<DbPropertie> dbRequest = new HttpEntity<>(newProperty);
        ResponseEntity<DbPropertie> crudResponse
        = (ResponseEntity<DbPropertie>) makeRequest(dbRequest, StringConstants.CRUD_POST_PROPERTIE_ENDPOINT,  DbPropertie.class);
        
        if(confirmResponse(crudResponse)){
        	Collection<String> key =null;
        	key = contextRefresher.refreshEnvironment();  
        	if(!key.equals(null)) {
            	log.info("Config server refrescado con exito");
            	log.info("Consiguiendo rutas de los micros a actualizar.");
            	String eurekaInstancesEndpoints = StringConstants.EUREKA_INSTANCIAS_ENDPOINT.concat("/").concat(canal);
            	log.info("url: "+eurekaInstancesEndpoints);

            	instanciasToUpdate = restTemplate.getForEntity(eurekaInstancesEndpoints, Object.class);
                
                log.info("Actualizar Instancias: " +instanciasToUpdate);
            	
                
                
                if(confirmResponse(instanciasToUpdate)){
                	Map<String, Object> body = (Map<String, Object>) instanciasToUpdate.getBody();

                	List<String> instancesUrls =(List<String>) body.get("instancesUrls");
                	log.info("urls: "+instancesUrls);
                	
                    List<String> instanciasActualizada = actualizarInstancias(instancesUrls);                
                	
                    response.put("actualizadas", instanciasActualizada);
                }
                
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        	}
        }    	
    	response.put("msg", "No se pudo refrescar el config server.");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

    private List<String> actualizarInstancias(List<String> instanciasToUpdate){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
        ExecutorService executor = Executors.newFixedThreadPool(instanciasToUpdate.size());
        List<Future<String>> instanciasRefrescadas = new ArrayList<>();
        List<String> instancias = new ArrayList<>();
        for (String baseUrl: instanciasToUpdate) {       
        	
            /// executor         
			Future<String> newEndpointCallThread = executor.submit(new RefreshMicroservicesPropertiesCallable(restTemplate, httpEntity, baseUrl));
			instanciasRefrescadas.add(newEndpointCallThread);   
        }
        
       while(true) {
    	   if (instanciasRefrescadas.stream().map(inst-> inst.isDone()).reduce((a, b)->a && b).get()) {
    		   log.info("Terminado");
			break;
		}
       }
        
        instanciasRefrescadas.stream().forEach(inst ->{
			try {
				log.info("Instancia refrescada: "+inst.get());
				instancias.add(inst.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

        return instancias;
    }

}
