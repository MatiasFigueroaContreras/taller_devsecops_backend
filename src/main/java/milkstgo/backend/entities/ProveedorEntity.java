package milkstgo.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String codigo;
    private String nombre;
    private String categoria;
    private String retencion;

    public boolean estaAfectoARetencion(){
        return retencion.equals("Si");
    }
}
