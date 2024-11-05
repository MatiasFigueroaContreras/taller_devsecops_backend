package milkstgo.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="datos_centro_acopio")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DatosCentroAcopioEntity {
    @Id
    private String id;
    @Column(name = "dias_envio_m_t")
    private Integer diasEnvioMyT;
    @Column(name = "dias_envio_m")
    private Integer diasEnvioM;
    @Column(name = "dias_envio_t")
    private Integer diasEnvioT;
    private Integer totalKlsLeche;
    private Integer variacionLeche;
    private Integer variacionGrasa;
    private Integer variacionSolidoTotal;
    @OneToOne
    private LaboratorioLecheEntity laboratorioLeche;
    @ManyToOne
    private ProveedorEntity proveedor;
    @ManyToOne
    private QuincenaEntity quincena;

    public Integer getTotalDias(){
        return diasEnvioMyT + diasEnvioM + diasEnvioT;
    }

    public Integer getPromedioKlsPorDia(){
        if(getTotalDias() == 0){
            return 0;
        }
        return totalKlsLeche / getTotalDias();
    }
}
