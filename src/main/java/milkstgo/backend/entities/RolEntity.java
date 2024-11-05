package milkstgo.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rol")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@SequenceGenerator(name = "rol_sequence", sequenceName = "rol_sequence", allocationSize = 1, initialValue = 4)
public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_sequence")
    private Long id;

    private String valor;
}
