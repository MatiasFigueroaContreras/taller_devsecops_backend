package milkstgo.backend.entities;

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

    public boolean estaAfectoARetencion(){
        return retencion.equals("Si");
    }
}
