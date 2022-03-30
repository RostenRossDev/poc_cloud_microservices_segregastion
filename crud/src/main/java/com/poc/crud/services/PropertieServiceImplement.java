package com.poc.crud.services;
import com.poc.crud.entities.Propertie;
import com.poc.crud.interfaces.PropertieServiceInterface;
import com.poc.crud.interfaces.PropertiesCrudIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertieServiceImplement implements PropertieServiceInterface {
    @Autowired
    PropertiesCrudIRepository repository;

    @Override
    public Propertie save(Propertie prop) {

        return repository.save(prop);
    }

    @Override
    public Propertie update(Propertie prop) {
        return repository.save(prop);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Propertie select(Long id) {
        return repository.findById(id).orElse(new Propertie());
    }

    @Override
    public List<Propertie> selectAll() {
        return repository.findAll();
    }
}
