package com.mbaigo.swingapp.service.order_service.repositories;

import com.mbaigo.swingapp.service.order_service.entities.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    // Spring va traduire ça en : SELECT * FROM commandes WHERE date_rendez_vous BETWEEN ? AND ? ORDER BY date_rendez_vous ASC
    List<Commande> findByDateRendezVousBetweenOrderByDateRendezVousAsc(LocalDate debut, LocalDate fin);
}
