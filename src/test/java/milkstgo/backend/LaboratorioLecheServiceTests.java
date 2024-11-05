package milkstgo.backend;

import milkstgo.backend.repositories.LaboratorioLecheRepository;
import milkstgo.backend.services.LaboratorioLecheService;
import milkstgo.backend.services.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import milkstgo.backend.entities.LaboratorioLecheEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@SpringBootTest
class LaboratorioLecheServiceTests {
    @Mock
    private LaboratorioLecheRepository laboratorioLecheRepositoryMock;
    @Mock
    private ProveedorService proveedorServiceMock;
    @InjectMocks
    private LaboratorioLecheService laboratorioLecheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    //Test para guardar lista de grasa y solidos totales, el cual a su vez utiliza guardar grasa y solido total
    void testGuardarListaGrasasSolidosTotales(){
        ProveedorEntity proveedor1 = new ProveedorEntity("12345", "Proveedor 1", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        LaboratorioLecheEntity grasaSolidoTotal1 = new LaboratorioLecheEntity();
        grasaSolidoTotal1.setPorcentajeGrasa(40);
        grasaSolidoTotal1.setPorcentajeSolidoTotal(35);
        grasaSolidoTotal1.setProveedor(proveedor1);
        grasaSolidoTotal1.setQuincena(quincena);

        ProveedorEntity proveedor2 = new ProveedorEntity("54321", "Proveedor 2", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal2 = new LaboratorioLecheEntity();
        grasaSolidoTotal2.setPorcentajeGrasa(60);
        grasaSolidoTotal2.setPorcentajeSolidoTotal(23);
        grasaSolidoTotal2.setProveedor(proveedor2);
        grasaSolidoTotal2.setQuincena(quincena);

        ArrayList<LaboratorioLecheEntity> grasasSolidosTotales = new ArrayList<>();
        grasasSolidosTotales.add(grasaSolidoTotal1);
        grasasSolidosTotales.add(grasaSolidoTotal2);

        laboratorioLecheService.guardarListaDatosLaboratorioLeche(grasasSolidosTotales, quincena);
        verify(laboratorioLecheRepositoryMock, times(1)).save(grasaSolidoTotal1);
        verify(laboratorioLecheRepositoryMock, times(1)).save(grasaSolidoTotal2);
    }

    @Test
    //Test validar lista grasa y solidos totales para el caso exitoso
    void testValidarListaGrasasSolidosTotalesExitoso() {
        ProveedorEntity proveedor1 = new ProveedorEntity("12345", "Proveedor 1", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal1 = new LaboratorioLecheEntity();
        grasaSolidoTotal1.setPorcentajeGrasa(40);
        grasaSolidoTotal1.setPorcentajeSolidoTotal(35);
        grasaSolidoTotal1.setProveedor(proveedor1);

        ProveedorEntity proveedor2 = new ProveedorEntity("54321", "Proveedor 2", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal2 = new LaboratorioLecheEntity();
        grasaSolidoTotal2.setPorcentajeGrasa(60);
        grasaSolidoTotal2.setPorcentajeSolidoTotal(23);
        grasaSolidoTotal2.setProveedor(proveedor2);

        ArrayList<LaboratorioLecheEntity> grasasSolidosTotales = new ArrayList<>();
        grasasSolidosTotales.add(grasaSolidoTotal1);
        grasasSolidosTotales.add(grasaSolidoTotal2);

        when(proveedorServiceMock.existeProveedor(proveedor1)).thenReturn(true);
        when(proveedorServiceMock.existeProveedor(proveedor2)).thenReturn(true);

        assertAll(() -> laboratorioLecheService.validarListaDatosLaboratorioLeche(grasasSolidosTotales));
    }

    @Test
    //Test para verificar que se lance una excepcion al tener un porcentaje de grasa no valido
    void testValidarGrasaSolidoTotalGrasaInvalida(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity();
        grasaSolidoTotal.setPorcentajeGrasa(300);
        grasaSolidoTotal.setPorcentajeSolidoTotal(35);
        grasaSolidoTotal.setProveedor(proveedor);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(true);
        Exception exception = assertThrows(Exception.class, () -> {
            laboratorioLecheService.validarDatosLaboratorioLeche(grasaSolidoTotal);
        });

        assertEquals("El porcentaje de grasa no es valido", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al tener un porcentaje de solido total no valido
    void testValidarGrasaSolidoTotalSolidoTotalInvalido(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity();
        grasaSolidoTotal.setPorcentajeGrasa(60);
        grasaSolidoTotal.setPorcentajeSolidoTotal(260);
        grasaSolidoTotal.setProveedor(proveedor);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(true);
        Exception exception = assertThrows(Exception.class, () -> {
            laboratorioLecheService.validarDatosLaboratorioLeche(grasaSolidoTotal);
        });

        assertEquals("El porcentaje de solido total no es valido", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al tener un proveedor no registrado
    void testValidarGrasaSolidoTotalSolidoTotalProveedorNoRegistrado(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity();
        grasaSolidoTotal.setPorcentajeGrasa(60);
        grasaSolidoTotal.setPorcentajeSolidoTotal(20);
        grasaSolidoTotal.setProveedor(proveedor);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(false);
        Exception exception = assertThrows(Exception.class, () -> {
            laboratorioLecheService.validarDatosLaboratorioLeche(grasaSolidoTotal);
        });

        assertEquals("Los proveedores tienen que estar registrados", exception.getMessage());
    }

    @Test
    //Test para verificar el funcionamiento de obtener grasa solido total por proveedor y quincena
    void testObtenerGrasaSolidoTotalPorProveedorQuincena() {
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity("12345-2023/03/1", 25, 32, proveedor, quincena);

        when(laboratorioLecheRepositoryMock.findByProveedorAndQuincena(proveedor, quincena)).thenReturn(Optional.of(grasaSolidoTotal));
        LaboratorioLecheEntity respuesta = laboratorioLecheService.obtenerLaboratorioLechePorProveedorQuincena(proveedor, quincena);
        assertEquals(grasaSolidoTotal, respuesta);
    }

    @Test
    //Test para verificar el lancamiento de la excepcion de obtener grasa solido total por proveedor y quincena
    void testObtenerGrasaSolidoTotalPorProveedorQuincenaNoExiste() {
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);

        when(laboratorioLecheRepositoryMock.findByProveedorAndQuincena(proveedor, quincena)).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> {
            laboratorioLecheService.obtenerLaboratorioLechePorProveedorQuincena(proveedor, quincena);
        });

        assertEquals("No existe datos de grasa y solido total para un proveedor dada la quincena ingresada", exception.getMessage());
    }

    @Test
    //Test para verificar el cuando existe grasa solido total por quincena
    void testExisteGrasaSolidoTotalPorQuincenaTrue(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        when(laboratorioLecheRepositoryMock.existsByQuincena(quincena)).thenReturn(true);
        assertTrue(laboratorioLecheService.existeLaboratorioLechePorQuincena(quincena));
    }

    @Test
    //Test para verificar el cuando NO existe grasa solido total por quincena
    void testExisteGrasaSolidoTotalPorQuincenaFalse(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        when(laboratorioLecheRepositoryMock.existsByQuincena(quincena)).thenReturn(false);
        assertFalse(laboratorioLecheService.existeLaboratorioLechePorQuincena(quincena));
    }
}
