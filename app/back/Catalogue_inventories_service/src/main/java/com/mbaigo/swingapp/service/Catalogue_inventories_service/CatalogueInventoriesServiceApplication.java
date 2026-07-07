package com.mbaigo.swingapp.service.Catalogue_inventories_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class CatalogueInventoriesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogueInventoriesServiceApplication.class, args);
	}

}
