package com.mbaigo.swingapp.service.customer.customer_service.models;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Calendar;
import java.util.Date;
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

    // Le mapping JSONB natif
    @JdbcTypeCode(SqlTypes.JSON)
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Double> mesures = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    private Client client;

    private String remarquesSpecifiques;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_prise", updatable = false) // updatable=false empêche la modification par erreur plus tard
    private Date datePrise;

    // --- LA MAGIE EST ICI ---
    @PrePersist
    protected void onCreate() {
        if (this.datePrise == null) {
            this.datePrise = Calendar.getInstance().getTime();
        }
    }
}
