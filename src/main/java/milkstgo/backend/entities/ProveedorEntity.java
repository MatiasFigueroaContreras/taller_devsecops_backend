package milkstgo.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedor")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProveedorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String codigo;
    private String nombre;
    private String categoria;
    private String retencion;
    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted;

    public boolean estaAfectoARetencion(){
        return retencion.equals("Si");
    }
}
