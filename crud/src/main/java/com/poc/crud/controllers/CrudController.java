package com.poc.crud.controllers;

import com.netflix.discovery.converters.Auto;
import com.poc.crud.entities.Propertie;
import com.poc.crud.services.PropertieServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("/crud")
public class CrudController {

    @Autowired
    PropertieServiceImplement propertieRepository;

    @PutMapping("/")
    public ResponseEntity<?> update(@RequestBody Propertie newProp) {
        Propertie prop = propertieRepository.update(newProp);
        HttpStatus status;
        Map<String, Object> res = new HashMap<>();
        if (!prop.getId().equals(null)) {
            res.put("property", prop);
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            res.put("error", "Servicio no disponible por el momento!. Intente mas tarde.");
        }
        return new ResponseEntity<Map<String, Object>>(res, status);
    }

    @PostMapping("/")
    public ResponseEntity<?> save(@RequestBody Propertie newProp) {
        Propertie prop = propertieRepository.save(newProp);
        HttpStatus status;
        Map<String, Object> res = new HashMap<>();
        if (!prop.getId().equals(null)) {
            res.put("property", prop);
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            res.put("error", "Servicio no disponible por el momento!. Intente mas tarde.");
        }
        return new ResponseEntity<Map<String, Object>>(res, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> selectById(@PathVariable(name = "id") Long id) {
        Propertie prop = propertieRepository.select(id);
        HttpStatus status;
        Map<String, Object> res = new HashMap<>();
        if (!prop.equals(null)) {
            res.put("property", prop);
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.NOT_FOUND;
            res.put("message", "No se encontro la property.");
        }
        return new ResponseEntity<Map<String, Object>>(res, status);
    }

    @GetMapping("/")
    public ResponseEntity<?> selectAll() {
        List<Propertie> props = propertieRepository.selectAll();
        HttpStatus status;
        Map<String, Object> res = new HashMap<>();
        if (!props.isEmpty()) {
            res.put("properties", props);
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.NOT_FOUND;
            res.put("message", "No se encontro la property.");
        }
        return new ResponseEntity<Map<String, Object>>(res, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name="id") Long id) {
        List<Propertie> props = propertieRepository.selectAll();
        HttpStatus status;
        Map<String, Object> res = new HashMap<>();
        try{
            propertieRepository.delete(id);
            status=HttpStatus.OK;
            res.put("message", "Property eliminada con exito.");
        }catch (Error e){
            status=HttpStatus.INTERNAL_SERVER_ERROR;
            res.put("cause", e.getCause());
            res.put("error", e.getMessage());
        }
        return new ResponseEntity<Map<String, Object>>(res, status);
    }
}