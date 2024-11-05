package milkstgo.backend.services;

import milkstgo.backend.entities.RolEntity;
import milkstgo.backend.entities.UsuarioEntity;
import milkstgo.backend.repositories.RolRepository;
import milkstgo.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    public UsuarioEntity registrarUsuario(UsuarioEntity usuario) {
        Optional<RolEntity> rol = this.rolRepository.findById(usuario.getRol().getId());
        if(rol.isEmpty()) {
            throw new IllegalArgumentException("No existe el rol ingresado");
        }

        if (this.usuarioRepository.existsByCorreo(usuario.getCorreo())){
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        if (!validarCorreoRegex(usuario.getCorreo())){
            throw new IllegalArgumentException("El correo no cumple con las condiciones");
        }

        String encodedPassword = this.encodePassword(usuario.getPassword());
        usuario.setPassword(encodedPassword);
        return this.usuarioRepository.save(usuario);
    }

    public UsuarioEntity obtenerUsuarioPorCorreo(String correo) {
        return this.usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario con el correo especificado"));
    }

    public boolean validarCorreoRegex(String correo){
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

    private String encodePassword(String password) {
        // Minimo de 10 factor de trabajo para el uso de bcrypt ASVS 4.03 V.2.4.4
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        return passwordEncoder.encode(password);
    }
}
