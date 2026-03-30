package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleResponse;

import java.util.List;

public interface ModeleService {
    ModeleResponse createModele(ModeleRequest request);
    List<ModeleResponse> getAllModeles();
    ModeleResponse getModeleById(Long id);
    ModeleResponse updateModele(Long id, ModeleRequest request);
}
