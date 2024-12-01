package milkstgo.backend;

import milkstgo.backend.repositories.ProveedorRepository;
import milkstgo.backend.services.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import milkstgo.backend.entities.ProveedorEntity;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProveedorServiceTests {
    @Mock
    private ProveedorRepository proveedorRepositoryMock;
    @InjectMocks
    private ProveedorService proveedorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //@Test
    //Test para verificar cuando se registra un proveedor correctamente
    //void testRegistrarProveedorExitoso() {
    //    ProveedorEntity proveedor = new ProveedorEntity();
    //    proveedor.setId(1L);
    //    proveedor.setCodigo("12345");
    //    proveedor.setNombre("Proveedor");
    //    proveedor.setCategoria("A");
    //    proveedor.setRetencion("Si");
    //    proveedor.setIsDeleted(false);

    //    when(proveedorRepositoryMock.findByCodigoAndIsDeletedFalse(proveedor.getCodigo())).thenReturn(Optional.of(proveedor));

    //    proveedorService.registrarProveedor(proveedor.getCodigo(),
    //                                         proveedor.getNombre(),
    //                                         proveedor.getCategoria(),
    //                                        proveedor.getRetencion());

    //    verify(proveedorRepositoryMock, times(1)).save(proveedor);

    //}

    @Test
    //Test para verificar que se lance una excepcion al registrar un proveedor con codigo distinto a 5 digitos
    void testRegistrarProveedorConCodigoInvalidoCaso1() {
        String codigo = "1234";
        String nombre = "Proveedor";
        String categoria = "B";
        String retencion = "No";

        Exception exception = assertThrows(Exception.class, () -> {
            proveedorService.registrarProveedor(codigo, nombre, categoria, retencion);
        });

        assertEquals("El codigo tiene que ser de 5 digitos numericos", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al registrar un proveedor con codigo no numerico
    void testRegistrarProveedorConCodigoInvalidoCaso2() {
        String codigo = "NoNum";
        String nombre = "Proveedor";
        String categoria = "B";
        String retencion = "No";

        Exception exception = assertThrows(Exception.class, () -> {
            proveedorService.registrarProveedor(codigo, nombre, categoria, retencion);
        });

        assertEquals("El codigo tiene que ser de 5 digitos numericos", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al registrar un proveedor con codigo numerico negativo
    void testRegistrarProveedorConCodigoInvalidoCaso3() {
        String codigo = "-1234";
        String nombre = "Proveedor";
        String categoria = "B";
        String retencion = "No";

        Exception exception = assertThrows(Exception.class, () -> {
            proveedorService.registrarProveedor(codigo, nombre, categoria, retencion);
        });

        assertEquals("El codigo tiene que ser de 5 digitos numericos", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al registrar un proveedor con una categoría no valida
    void testRegistrarProveedorConCategoriaInvalida() {
        String codigo = "12345";
        String nombre = "Proveedor";
        String categoria = "E";
        String retencion = "No";

        Exception exception = assertThrows(Exception.class, () -> {
            proveedorService.registrarProveedor(codigo, nombre, categoria, retencion);
        });

        assertEquals("La categoria ingresada no es valida", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al registrar un proveedor con un valor no valido para retencion
    void testRegistrarProveedorConRetencionInvalida() {
        String codigo = "12345";
        String nombre = "Proveedor";
        String categoria = "C";
        String retencion = "retencion_invalida";

        Exception exception = assertThrows(Exception.class, () -> {
            proveedorService.registrarProveedor(codigo, nombre, categoria, retencion);
        });

        assertEquals("El afecto a retencion ingresado no es valido", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al registrar un proveedor existente
    void testRegistrarProveedorExistente() {
        ProveedorEntity proveedor = new ProveedorEntity();
        proveedor.setId(1L);
        proveedor.setCodigo("12345");
        proveedor.setNombre("Proveedor");
        proveedor.setCategoria("A");
        proveedor.setRetencion("Si");
        proveedor.setIsDeleted(false);

        when(proveedorRepositoryMock.existsByCodigoAndIsDeletedFalse("12345")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            proveedorService.registrarProveedor(proveedor.getCodigo(),
                    proveedor.getNombre(),
                    proveedor.getCategoria(),
                    proveedor.getRetencion());
        });

        assertEquals("El proveedor ya se encuentra registrado", exception.getMessage());
    }

    @Test
    //Test para verificar que se obtengan proveedores
    void testObtenerProveedores() {
        when(proveedorRepositoryMock.findAll()).thenReturn(new ArrayList<>());
        assertEquals(new ArrayList<>(), proveedorService.obtenerProveedores());
    }

    @Test
    //Test para verificar que existan proveedores
    void testExisteProveedorTrue() {
        ProveedorEntity proveedor = new ProveedorEntity(1L, "12345", "Proveedor", "A", "Si", false);

        when(proveedorRepositoryMock.existsByCodigoAndIsDeletedFalse(proveedor.getCodigo())).thenReturn(true);
        boolean resultado = proveedorService.existeProveedor(proveedor);
        assertTrue(resultado);
    }

    @Test
    //Test para verificar que opcion que no existen proveedores
    void testExisteProveedorFalse() {
        ProveedorEntity proveedor = new ProveedorEntity(1L, "12345", "Proveedor", "A", "Si", false);

        when(proveedorRepositoryMock.existsByCodigo(proveedor.getCodigo())).thenReturn(false);
        boolean resultado = proveedorService.existeProveedor(proveedor);
        assertFalse(resultado);
    }
}

