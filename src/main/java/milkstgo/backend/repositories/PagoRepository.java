package milkstgo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import milkstgo.backend.entities.PagoEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<PagoEntity, String> {
    boolean existsByQuincena(QuincenaEntity quincena);

    List<PagoEntity> findAllByQuincena(QuincenaEntity quincena);
    List<PagoEntity> findAllByOrderByQuincenaDescProveedorCodigoAsc();
}
