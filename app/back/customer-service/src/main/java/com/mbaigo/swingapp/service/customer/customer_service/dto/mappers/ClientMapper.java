package com.mbaigo.swingapp.service.customer.customer_service.dto.mappers;

import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientResponseDTO;
import com.mbaigo.swingapp.service.customer.customer_service.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientResponseDTO toDto(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "telephone", source = "telephoneNormalise")
    Client toEntity(ClientRequestDTO dto, String telephoneNormalise);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "telephone", source = "telephoneNormalise")
    void updateEntityFromDto(@MappingTarget Client client, ClientRequestDTO dto, String telephoneNormalise);
}
