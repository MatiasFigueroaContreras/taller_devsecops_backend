package milkstgo.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grasa_solido_total")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LaboratorioLecheEntity {
    @Id
    private String id;
    private Integer porcentajeGrasa;
    private Integer porcentajeSolidoTotal;
    @ManyToOne
    private ProveedorEntity proveedor;
    @ManyToOne
    private QuincenaEntity quincena;
}
