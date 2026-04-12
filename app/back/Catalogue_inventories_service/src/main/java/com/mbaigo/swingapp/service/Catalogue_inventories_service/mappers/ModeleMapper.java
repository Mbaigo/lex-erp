package com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.LigneNomenclatureRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.LigneNomenclatureResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.LigneNomenclature;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Modele;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ArticleMapper.class})
public interface ModeleMapper {

    @Mapping(target = "lignesNomenclature", source = "lignes")
    Modele toEntity(ModeleRequest request);

    @Mapping(target = "article.id", source = "articleId")
    LigneNomenclature toLigneEntity(LigneNomenclatureRequest request);

    @Mapping(target = "coutDeBase", expression = "java(entity.getCoutDeBase())")
    ModeleResponse toResponse(Modele entity);

    @Mapping(target = "coutLigne", expression = "java(entity.getCoutLigne())")
    LigneNomenclatureResponse toLigneResponse(LigneNomenclature entity);

    @Mapping(target = "lignesNomenclature", source = "lignes")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(ModeleRequest request, @MappingTarget Modele entity);
}
