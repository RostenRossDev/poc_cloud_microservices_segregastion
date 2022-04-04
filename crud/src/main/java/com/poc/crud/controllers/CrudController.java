package com.poc.crud.controllers;

import com.poc.crud.entities.Propertie;
import com.poc.crud.services.PropertieServiceImplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CrudController {
	
	private Logger log = LoggerFactory.getLogger(CrudController.class);
	
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
        HttpStatus status;
        Map<String, Object> res = new HashMap<>();
        log.info("App: "+newProp.getApplication());
        log.info("key: "+newProp.getLabel());
        log.info("profile: "+newProp.getProfile());
        log.info("value: "+newProp.getValue());

        //vemos si existe la properti en la db
        List<Propertie> persistedProps = propertieRepository
        		.selectByKeyAndProfileAndLabelAndApplication(newProp);
        log.info("Lista de prop: "+persistedProps.toString());
        
        //Si existe, se modifica el value por el value nuevo
        if(persistedProps.size() > 0) {
        	Propertie persistedProp = persistedProps.get(0);
        	newProp.setId(persistedProp.getId());
        }
        
        log.info("newProp: "+newProp.toString());
        log.info("App: "+newProp.getApplication());
        log.info("key: "+newProp.getLabel());
        log.info("profile: "+newProp.getProfile());
        log.info("value: "+newProp.getValue());
        //se guarda la properti
        Propertie prop = propertieRepository.save(newProp);
        
        //chekamos si se guardo la prop y respondemos segun corresponda
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