package milkstgo.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import milkstgo.backend.entities.QuincenaEntity;

@Repository
public interface QuincenaRepository extends JpaRepository<QuincenaEntity, String> {
}
