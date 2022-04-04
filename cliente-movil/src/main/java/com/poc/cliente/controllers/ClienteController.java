package com.poc.cliente.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RefreshScope
@RestController
public class ClienteController {

    @Autowired
    private Environment env;


    @GetMapping("/config")
    public ResponseEntity<?> getConfiguracion(){
        Map<String, Object> res = new HashMap<>();
        res.put("autor", env.getProperty("configuracion.author.name"));
        res.put("texto", env.getProperty("configuracion.texto"));
        res.put("pais", env.getProperty("pais"));
        res.put("properties", env.toString());

        return new ResponseEntity<Map<String, Object>>(res, HttpStatus.OK);
    }
}
