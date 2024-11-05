package milkstgo.backend;

import milkstgo.backend.repositories.AcopioLecheRepository;
import milkstgo.backend.services.AcopioLecheService;
import milkstgo.backend.services.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import milkstgo.backend.entities.AcopioLecheEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AcopioLecheServiceTests {
    @Mock
    private AcopioLecheRepository acopioLecheRepositoryMock;
    @Mock
    private ProveedorService proveedorServiceMock;
    @InjectMocks
    private AcopioLecheService acopioLecheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    //Test para guardar lista de acopios leche, el cual a su vez utiliza guardar acopio leche
    void testGuardarAcopiosLeche(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);

        AcopioLecheEntity acopioLeche1 = new AcopioLecheEntity();
        acopioLeche1.setTurno("M");
        acopioLeche1.setCantidadLeche(250);
        acopioLeche1.setFecha(new Date());
        acopioLeche1.setProveedor(proveedor);
        acopioLeche1.setQuincena(quincena);

        AcopioLecheEntity acopioLeche2 = new AcopioLecheEntity();
        acopioLeche2.setTurno("T");
        acopioLeche2.setCantidadLeche(120);
        acopioLeche2.setFecha(new Date());
        acopioLeche2.setProveedor(proveedor);
        acopioLeche2.setQuincena(quincena);

        ArrayList<AcopioLecheEntity> acopiosLeche = new ArrayList<>();
        acopiosLeche.add(acopioLeche1);
        acopiosLeche.add(acopioLeche2);

        acopioLecheService.guardarAcopiosLeches(acopiosLeche);
        verify(acopioLecheRepositoryMock, times(1)).save(acopioLeche1);
        verify(acopioLecheRepositoryMock, times(1)).save(acopioLeche2);
    }

    @Test
    //Test para verificar el funcionamiento de obtener acopios leche por proveedor y quincena
    void testObtenerAcopiosLechePorProveedorQuincena(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);

        when(acopioLecheRepositoryMock.findAllByProveedorAndQuincena(proveedor, quincena)).thenReturn(new ArrayList<>());
        List<AcopioLecheEntity> respuesta =  acopioLecheService.obtenerAcopiosLechePorProveedorQuincena(proveedor, quincena);
        assertEquals(new ArrayList<>(), respuesta);
    }

    @Test
    //Test para verificar el cuando existen acopios de leche por quincena
    void testExistenAcopiosLechePorQuincenaTrue(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        when(acopioLecheRepositoryMock.existsByQuincena(quincena)).thenReturn(true);
        assertTrue(acopioLecheService.existenAcopiosLechePorQuincena(quincena));
    }

    @Test
    //Test para verificar el cuando NO existen acopios de leche por quincena
    void testExistenAcopiosLechePorQuincenaFalse(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        when(acopioLecheRepositoryMock.existsByQuincena(quincena)).thenReturn(false);
        assertFalse(acopioLecheService.existenAcopiosLechePorQuincena(quincena));
    }

    @Test
    //Test validar lista acopio leche quincena con el caso de acopios validos
    void testValidarListaAcopioLecheQuincena() throws Exception {
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        AcopioLecheEntity acopioLeche1 = new AcopioLecheEntity();
        acopioLeche1.setTurno("M");
        acopioLeche1.setCantidadLeche(250);
        acopioLeche1.setFecha(dateFormat.parse("2023/03/10"));
        acopioLeche1.setProveedor(proveedor);

        AcopioLecheEntity acopioLeche2 = new AcopioLecheEntity();
        acopioLeche2.setTurno("T");
        acopioLeche2.setCantidadLeche(120);
        acopioLeche2.setFecha(dateFormat.parse("2023/03/10"));
        acopioLeche2.setProveedor(proveedor);

        ArrayList<AcopioLecheEntity> acopiosLeche = new ArrayList<>();
        acopiosLeche.add(acopioLeche1);
        acopiosLeche.add(acopioLeche2);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(true);

        assertAll(() -> acopioLecheService.validarListaAcopioLecheQuincena(acopiosLeche, quincena));
    }

    @Test
    //Test para verificar que se lance una excepcion al tener un turno no valido
    void testValidarAcopioLecheTurnoInvalido(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);

        AcopioLecheEntity acopioLeche = new AcopioLecheEntity();
        acopioLeche.setTurno("F");
        acopioLeche.setCantidadLeche(250);
        acopioLeche.setFecha(new Date());
        acopioLeche.setProveedor(proveedor);
        acopioLeche.setQuincena(quincena);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(true);
        Exception exception = assertThrows(Exception.class, () -> {
            acopioLecheService.validarAcopioLeche(acopioLeche);
        });

        assertEquals("Algun turno no es valido, debe ser M o T", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al tener kilos de leche no valido
    void testValidarAcopioLecheKlsLecheInvalido(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);

        AcopioLecheEntity acopioLeche = new AcopioLecheEntity();
        acopioLeche.setTurno("M");
        acopioLeche.setCantidadLeche(-1);
        acopioLeche.setFecha(new Date());
        acopioLeche.setProveedor(proveedor);
        acopioLeche.setQuincena(quincena);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(true);
        Exception exception = assertThrows(Exception.class, () -> {
            acopioLecheService.validarAcopioLeche(acopioLeche);
        });

        assertEquals("Los kilos de leche tienen que ser positivos", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al tener una fecha no valida
    void testValidarAcopioLecheFechaInvalida() throws Exception{
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2022/03/1", 2022, 03, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        AcopioLecheEntity acopioLeche = new AcopioLecheEntity();
        acopioLeche.setTurno("M");
        acopioLeche.setCantidadLeche(250);
        acopioLeche.setFecha(dateFormat.parse("2023/03/14"));
        acopioLeche.setProveedor(proveedor);
        acopioLeche.setQuincena(quincena);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(true);
        Exception exception = assertThrows(Exception.class, () -> {
            acopioLecheService.validarAcopioLeche(acopioLeche);
        });

        assertEquals("Las fechas ingresadas tienen que coincidir con la quincena", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al tener un proveedor no registrado
    void testValidarAcopioLecheProveedorNoRegistrado() throws Exception{
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        AcopioLecheEntity acopioLeche = new AcopioLecheEntity();
        acopioLeche.setTurno("M");
        acopioLeche.setCantidadLeche(250);
        acopioLeche.setFecha(dateFormat.parse("2023/03/10"));
        acopioLeche.setProveedor(proveedor);
        acopioLeche.setQuincena(quincena);

        when(proveedorServiceMock.existeProveedor(proveedor)).thenReturn(false);
        Exception exception = assertThrows(Exception.class, () -> {
            acopioLecheService.validarAcopioLeche(acopioLeche);
        });

        assertEquals("Los proveedores tienen que estar registrados", exception.getMessage());
    }
}
