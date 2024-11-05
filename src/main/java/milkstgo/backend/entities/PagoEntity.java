package milkstgo.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="pago")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PagoEntity {
    @Id
    private String id;
    private Integer pagoLeche;
    private Integer pagoGrasa;
    private Integer pagoSolidoTotal;
    private Integer bonificacionFrecuencia;
    private Integer dctoVariacionLeche;
    private Integer dctoVariacionGrasa;
    private Integer dctoVariacionSolidoTotal;
    private Integer pagoTotal;
    private Integer montoRetencion;
    private Integer montoFinal;
    @ManyToOne
    private ProveedorEntity proveedor;
    @ManyToOne
    private QuincenaEntity quincena;
    @ManyToOne
    private DatosCentroAcopioEntity datosCentroAcopio;

    public Integer getPagoAcopioLeche(){
        return pagoLeche + pagoGrasa + pagoSolidoTotal + bonificacionFrecuencia;
    }

    public Integer getDescuentos(){
        return dctoVariacionGrasa + dctoVariacionLeche + dctoVariacionSolidoTotal;
    }
}
