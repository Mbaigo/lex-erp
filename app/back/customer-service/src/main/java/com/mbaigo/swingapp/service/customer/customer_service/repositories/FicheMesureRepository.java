package com.mbaigo.swingapp.service.customer.customer_service.repositories;

import com.mbaigo.swingapp.service.customer.customer_service.models.FicheMesure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FicheMesureRepository extends JpaRepository<FicheMesure, Long> {
    // Le mot clé magique : OrderBy + NomDuChamp + Desc
    List<FicheMesure> findByClientIdOrderByDatePriseDesc(Long clientId);
}
