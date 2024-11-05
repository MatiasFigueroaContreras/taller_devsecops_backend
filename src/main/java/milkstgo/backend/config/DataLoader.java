package milkstgo.backend.config;

import milkstgo.backend.entities.RolEntity;
import milkstgo.backend.entities.UsuarioEntity;
import milkstgo.backend.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    /*@Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.correo}")
    private String adminCorreo;
    @Value("${admin.nombre}")
    private String adminNombre;
    @Value("${admin.apellido_paterno}")
    private String adminApellidoPaterno;
    @Value("${admin.apellido_materno}")
    private String adminApellidoMaterno;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String jpaHibernateDDL;

     */
    @Autowired
    private Environment env;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        // Obtener el valor de "spring.jpa.hibernate.ddl-auto"
        String jpaHibernateDDL = env.getProperty("spring.jpa.hibernate.ddl-auto");

        // Obtener valores de las propiedades de admin
        String adminCorreo = env.getProperty("admin.correo");
        String adminPassword = env.getProperty("admin.password");
        String adminNombre = env.getProperty("admin.nombre");
        String adminApellidoPaterno = env.getProperty("admin.apellido_paterno");
        String adminApellidoMaterno = env.getProperty("admin.apellido_materno");
        if(jpaHibernateDDL.equals("create")) {
            RolEntity adminRol = new RolEntity(1L, "ADMINISTRADOR");
            UsuarioEntity admin = UsuarioEntity.builder()
                    .rol(adminRol)
                    .correo(adminCorreo)
                    .nombre(adminNombre)
                    .apellidoPaterno(adminApellidoPaterno)
                    .apellidoMaterno(adminApellidoMaterno)
                    .password(adminPassword).build();
            try {
                usuarioService.registrarUsuario(admin);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
