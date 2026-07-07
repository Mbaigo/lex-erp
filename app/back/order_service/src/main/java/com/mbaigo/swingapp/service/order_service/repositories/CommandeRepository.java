package com.mbaigo.swingapp.service.order_service.repositories;

import com.mbaigo.swingapp.service.order_service.entities.Commande;
import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    // Spring va traduire ça en : SELECT * FROM commandes WHERE date_rendez_vous BETWEEN ? AND ? ORDER BY date_rendez_vous ASC
    //List<Commande> findByDateRendezVousBetweenOrderByDateRendezVousAsc(LocalDate debut, LocalDate fin);
    // NOUVEAU : Pagination par Statut
    Page<Commande> findByStatut(StatutCommande statut, Pageable pageable);

    // MODIFIÉ : Ajout du Pageable pour les rendez-vous
    Page<Commande> findByDateRendezVousBetween(LocalDate debut, LocalDate fin, Pageable pageable);
}
