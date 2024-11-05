package milkstgo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import milkstgo.backend.entities.ProveedorEntity;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, String> {

}
