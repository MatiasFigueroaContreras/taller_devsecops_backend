package milkstgo.backend.authentication.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;

    public UserResponse(User user) {
        this.nombre = user.getNombre();
        this.apellidoPaterno = user.getApellidoPaterno();
        this.apellidoMaterno = user.getApellidoMaterno();
        this.correo = user.getCorreo();
    }
}
