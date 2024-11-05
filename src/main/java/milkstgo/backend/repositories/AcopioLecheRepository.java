package milkstgo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import milkstgo.backend.entities.AcopioLecheEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.List;

@Repository
public interface AcopioLecheRepository extends JpaRepository<AcopioLecheEntity, String> {
    List<AcopioLecheEntity> findAllByProveedorAndQuincena(ProveedorEntity proveedor, QuincenaEntity quincena);
    boolean existsByQuincena(QuincenaEntity quincena);
}
