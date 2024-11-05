package milkstgo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import milkstgo.backend.entities.ProveedorEntity;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    Optional<ProveedorEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
