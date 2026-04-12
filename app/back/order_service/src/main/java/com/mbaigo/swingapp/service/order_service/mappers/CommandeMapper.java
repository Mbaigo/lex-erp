package com.mbaigo.swingapp.service.order_service.mappers;

import com.mbaigo.swingapp.service.order_service.dto.CommandeResponse;
import com.mbaigo.swingapp.service.order_service.dto.LigneMateriauCommandeResponse;
import com.mbaigo.swingapp.service.order_service.entities.Commande;
import com.mbaigo.swingapp.service.order_service.entities.LigneMateriauCommande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommandeMapper {

    // Convertit la commande globale
    CommandeResponse toResponse(Commande entity);

    // Convertit chaque ligne (MapStruct l'appelle automatiquement pour la liste)
    // On force l'exécution de la méthode getCoutLigne() de l'entité pour remplir le DTO
    @Mapping(target = "coutLigne", expression = "java(entity.getCoutLigne())")
    LigneMateriauCommandeResponse toLigneResponse(LigneMateriauCommande entity);
}
