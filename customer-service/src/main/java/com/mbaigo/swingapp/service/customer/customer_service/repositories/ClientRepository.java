package com.mbaigo.swingapp.service.customer.customer_service.repositories;

import com.mbaigo.swingapp.service.customer.customer_service.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByTelephone(String telephone);
    Optional<Client> findByTelephone(String telephone);
}
