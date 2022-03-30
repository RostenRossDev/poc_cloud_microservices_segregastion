package com.poc.eurekaServer.controllers;

import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.StatusResource;
import com.netflix.eureka.util.StatusInfo;
import com.poc.eurekaServer.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("/eureka")
public class EurekaCustomController {
    private Logger log = LoggerFactory.getLogger(EurekaCustomController.class);

    @Autowired
    Environment env;

    @GetMapping("/instancias/{appName}")
    public ResponseEntity<?> getInstancias(@PathVariable(name = "appName") String appName){
        Map<String, Object> response = new HashMap<>();
        HttpStatus status ;
        StatusInfo statusInfo;
        PeerAwareInstanceRegistryImpl registry = (PeerAwareInstanceRegistryImpl) getRegistry();
        List<com.netflix.discovery.shared.Application> sortedApplications = getRegistry().getSortedApplications();

        try {
            statusInfo = new StatusResource().getStatusInfo();

            //log.info("env: "+env.toString());
            log.info("storedApplications: "+sortedApplications);
            List<String> instancesUrls = new ArrayList<>();
            sortedApplications.stream().forEach(instance -> {
                if(instance.getName().equals(appName.toUpperCase())){
                    response.put("instances", instance);
                    instance.getInstances().stream().forEach(inst -> {
                        instancesUrls.add(inst.getHomePageUrl());
                    });
                    response.put("instancesUrls", instancesUrls);
                }
            });
            status = HttpStatus.OK;
        }
        catch (Exception e) {
            statusInfo = StatusInfo.Builder.newBuilder().isHealthy(false).build();
            response.put("error", "No se pudieron cargar las instancias.");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map<String, Object>>(response, status);
    }

    private PeerAwareInstanceRegistry getRegistry() {
        return getServerContext().getRegistry();
    }
    private EurekaServerContext getServerContext() {
        return EurekaServerContextHolder.getInstance().getServerContext();
    }

}
