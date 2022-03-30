package com.poc.crud.interfaces;

import com.poc.crud.entities.Propertie;

import java.util.List;

public interface PropertieServiceInterface {

    Propertie save (Propertie prop);

    Propertie update (Propertie prop);

    void delete(Long id);

    Propertie select(Long id);

    List<Propertie> selectAll();
}
