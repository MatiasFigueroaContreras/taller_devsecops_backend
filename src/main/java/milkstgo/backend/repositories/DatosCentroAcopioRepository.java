package milkstgo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import milkstgo.backend.entities.DatosCentroAcopioEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.Optional;

@Repository
public interface DatosCentroAcopioRepository extends JpaRepository<DatosCentroAcopioEntity, String> {
    Optional<DatosCentroAcopioEntity> findByProveedorAndQuincena(ProveedorEntity proveedor, QuincenaEntity quincena);
}
