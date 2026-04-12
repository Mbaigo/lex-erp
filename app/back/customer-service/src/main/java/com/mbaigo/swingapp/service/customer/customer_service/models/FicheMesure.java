package com.mbaigo.swingapp.service.customer.customer_service.models;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "fiches_mesure")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheMesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomProjet;

    @Column(nullable = false)
    private LocalDate datePrise;

    // Le mapping JSONB natif
    @JdbcTypeCode(SqlTypes.JSON)
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Double> valeurs = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    private Client client;
}
