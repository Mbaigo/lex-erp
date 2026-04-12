package  com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // Ex: TIS, FIL, PREST

    @Column(nullable = false, length = 100)
    private String nom; // Ex: Tissus, Fils, Prestations

    @Column(length = 255)
    private String description;
}