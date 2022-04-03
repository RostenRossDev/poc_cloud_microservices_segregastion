package com.poc.crud.interfaces;

import com.poc.crud.entities.Propertie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertiesCrudIRepository extends JpaRepository<Propertie, Long> {

	 List<Propertie> findByKeyAndProfileAndLabelAndApplication(String key, String profile, String label, String application);
}
