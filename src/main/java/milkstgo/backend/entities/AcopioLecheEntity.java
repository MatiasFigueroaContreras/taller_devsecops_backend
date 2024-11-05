package milkstgo.backend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "acopio_leche")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AcopioLecheEntity {
    @Id
    private String id;
    private String turno;
    private Integer cantidadLeche;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date fecha;
    @ManyToOne
    private ProveedorEntity proveedor;
    @ManyToOne
    private QuincenaEntity quincena;
}
