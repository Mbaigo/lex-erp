package com.mbaigo.swingapp.service.order_service.services;

import com.mbaigo.swingapp.service.order_service.dto.CommandeRequest;
import com.mbaigo.swingapp.service.order_service.dto.CommandeResponse;
import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommandeService {

    // US 5.1, 5.2 & 5.3 : Le cœur du réacteur
    CommandeResponse createCommande(CommandeRequest request);

    // Méthodes classiques
    CommandeResponse getCommandeById(Long id);

    List<CommandeResponse> getAllCommandes();
    public CommandeResponse annulerCommande(Long id);
    public CommandeResponse updateStatut(Long id, StatutCommande nouveauStatut);
    Page<CommandeResponse> getCommandesParStatut(StatutCommande statut, Pageable pageable);
    Page<CommandeResponse> getRendezVousParPeriode(String periode, Pageable pageable);
}
