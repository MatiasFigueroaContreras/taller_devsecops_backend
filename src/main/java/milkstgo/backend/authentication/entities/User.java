package milkstgo.backend.authentication.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import milkstgo.backend.entities.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    private Long id;
    private String username;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String password;
    private String role;

    public User(UsuarioEntity usuario) {
        this.id = usuario.getId();
        this.username = usuario.getCorreo();
        this.nombre = usuario.getNombre();
        this.apellidoPaterno = usuario.getApellidoPaterno();
        this.apellidoMaterno = usuario.getApellidoMaterno();
        this.correo = usuario.getCorreo();
        this.password = usuario.getPassword();
        this.role = usuario.getRol().getValor();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }



}
