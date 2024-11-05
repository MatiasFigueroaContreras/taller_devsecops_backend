package milkstgo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import milkstgo.backend.entities.LaboratorioLecheEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.Optional;

@Repository
public interface LaboratorioLecheRepository extends JpaRepository<LaboratorioLecheEntity, String> {
    Optional<LaboratorioLecheEntity> findByProveedorAndQuincena(ProveedorEntity proveedor, QuincenaEntity quincena);
    boolean existsByQuincena(QuincenaEntity quincena);
}
