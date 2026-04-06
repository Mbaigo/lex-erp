package com.mbaigo.swingapp.service.order_service.repositories;

import com.mbaigo.swingapp.service.order_service.entities.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
