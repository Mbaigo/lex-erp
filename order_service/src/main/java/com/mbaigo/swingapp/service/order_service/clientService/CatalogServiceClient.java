package com.mbaigo.swingapp.service.order_service.clientService;

import com.mbaigo.swingapp.service.order_service.clientDto.CatalogArticleDTO;
import com.mbaigo.swingapp.service.order_service.clientDto.CatalogModeleDTO;
import com.mbaigo.swingapp.service.order_service.dto.reStock.RestockItemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

// name = nom du microservice (utile plus tard si tu utilises un Service Discovery comme Eureka)
// url = l'adresse en dur pour l'instant pendant notre développement local
@FeignClient(name = "catalog-inventory-service", url = "http://localhost:8082")
public interface CatalogServiceClient {

    // On imite exactement le endpoint du CatalogController !
    @GetMapping("/api/modeles/{id}")
    CatalogModeleDTO getModeleById(@PathVariable("id") Long id);

    @GetMapping("/api/articles/{id}")
    CatalogArticleDTO getArticleById(@PathVariable("id") Long id);

    @PostMapping("/api/articles/batch")
    List<CatalogArticleDTO> getArticlesInBatch(@RequestBody List<Long> ids);

    // NOUVEL ENDPOINT POUR LE ROLLBACK (US 6.2)
    @PostMapping("/api/articles/stock/restock-batch")
    void restockArticles(@RequestBody List<RestockItemRequest> requests);
}
