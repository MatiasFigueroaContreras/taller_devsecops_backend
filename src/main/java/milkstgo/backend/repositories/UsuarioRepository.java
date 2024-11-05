package milkstgo.backend.repositories;

import milkstgo.backend.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Boolean existsByCorreo(String correo);

    Optional<UsuarioEntity> findByCorreo(String correo);
}
