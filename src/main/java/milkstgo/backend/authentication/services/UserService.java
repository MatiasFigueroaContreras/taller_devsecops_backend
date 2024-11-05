package milkstgo.backend.authentication.services;

import milkstgo.backend.authentication.entities.User;
import milkstgo.backend.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UsuarioService usuarioService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User(usuarioService.obtenerUsuarioPorCorreo(username));
    }
}
