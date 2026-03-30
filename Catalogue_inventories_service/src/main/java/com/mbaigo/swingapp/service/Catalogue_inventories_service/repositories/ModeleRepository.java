package com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Modele;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeleRepository extends JpaRepository<Modele, Long> {
    boolean existsByReference(String reference);
    Optional<Modele> findByReference(String reference);
}
