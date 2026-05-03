package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementRequestDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockMovementService {
    StockMovementResponseDTO createMovement(StockMovementRequestDTO dto);
    Page<StockMovementResponseDTO> getHistory(
            String reference,
            String designation,
            Pageable pageable
    );
    Page<StockMovementResponseDTO> getHistoryByArticleId(
            Long articleId,
            Pageable pageable
    );


}
