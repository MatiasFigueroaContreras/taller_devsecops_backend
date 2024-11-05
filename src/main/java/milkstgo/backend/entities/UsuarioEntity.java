package milkstgo.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@SequenceGenerator(name = "usuario_sequence", sequenceName = "usuario_sequence", allocationSize = 1, initialValue = 4)
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_sequence")
    @Column(name = "id")
    private Long id;

    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String password;
    @ManyToOne
    private RolEntity rol;

    public String getNombreCompleto() {
        return nombre + " " + apellidoPaterno + " " + apellidoMaterno;
    }
}
