package milkstgo.backend;

import milkstgo.backend.entities.*;
import milkstgo.backend.repositories.DatosCentroAcopioRepository;
import milkstgo.backend.services.AcopioLecheService;
import milkstgo.backend.services.DatosCentroAcopioService;
import milkstgo.backend.services.LaboratorioLecheService;
import milkstgo.backend.services.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@SpringBootTest
class DatosCentroAcopioServiceTests {
    @Mock
    DatosCentroAcopioRepository datosCentroAcopioRepositoryMock;
    @Mock
    AcopioLecheService acopioLecheServiceMock;
    @Mock
    LaboratorioLecheService laboratorioLecheServiceMock;
    @Mock
    ProveedorService proveedorServiceMock;
    @InjectMocks
    DatosCentroAcopioService datosCentroAcopioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarListaDatosCA(){
        ProveedorEntity proveedor1 = new ProveedorEntity("12345", "Proveedor 1", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        LaboratorioLecheEntity grasaSolidoTotal1 = new LaboratorioLecheEntity("12345-2023/03/1", 25, 32, proveedor1, quincena);
        DatosCentroAcopioEntity datosCentroAcopio1 = new DatosCentroAcopioEntity();
        datosCentroAcopio1.setDiasEnvioMyT(10);
        datosCentroAcopio1.setDiasEnvioM(1);
        datosCentroAcopio1.setDiasEnvioT(1);
        datosCentroAcopio1.setTotalKlsLeche(1254);
        datosCentroAcopio1.setVariacionLeche(10);
        datosCentroAcopio1.setVariacionGrasa(4);
        datosCentroAcopio1.setVariacionSolidoTotal(-30);
        datosCentroAcopio1.setLaboratorioLeche(grasaSolidoTotal1);
        datosCentroAcopio1.setProveedor(proveedor1);
        datosCentroAcopio1.setQuincena(quincena);

        ProveedorEntity proveedor2 = new ProveedorEntity("54321", "Proveedor 2", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal2 = new LaboratorioLecheEntity("54321-2023/03/1", 25, 32, proveedor2, quincena);
        DatosCentroAcopioEntity datosCentroAcopio2 = new DatosCentroAcopioEntity();
        datosCentroAcopio2.setDiasEnvioMyT(10);
        datosCentroAcopio2.setDiasEnvioM(1);
        datosCentroAcopio2.setDiasEnvioT(1);
        datosCentroAcopio2.setTotalKlsLeche(1254);
        datosCentroAcopio2.setVariacionLeche(10);
        datosCentroAcopio2.setVariacionGrasa(4);
        datosCentroAcopio2.setVariacionSolidoTotal(-30);
        datosCentroAcopio2.setLaboratorioLeche(grasaSolidoTotal2);
        datosCentroAcopio2.setProveedor(proveedor2);
        datosCentroAcopio2.setQuincena(quincena);

        ArrayList<DatosCentroAcopioEntity> listaDatosCa = new ArrayList<>();
        listaDatosCa.add(datosCentroAcopio1);
        listaDatosCa.add(datosCentroAcopio2);

        datosCentroAcopioService.guardarListaDatosCA(listaDatosCa);
        verify(datosCentroAcopioRepositoryMock, times(1)).save(datosCentroAcopio1);
        verify(datosCentroAcopioRepositoryMock, times(1)).save(datosCentroAcopio2);
    }

    @Test
    //Test que prueba el caso exitoso de calcular datos del centro de acopio por quincena,
    // tomando las funciones completas de calcularDatosCAPorProveedorQuincena, calcularDatosAcopioLeche
    // y solo un caso de calcularVariacionesDatosCA y obtenerDatosCAPorProveedorQuincena
    void testCalcularDatosCAPorQuincena() throws Exception {
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        AcopioLecheEntity acopioLeche1 = new AcopioLecheEntity(
                proveedor.getCodigo() +  "-2023/03/01-" + "M",
                "M",
                100,
                dateFormat.parse("2023/03/01"),
                proveedor,
                quincena);
        AcopioLecheEntity acopioLeche2 = new AcopioLecheEntity(
                proveedor.getCodigo() +  "-2023/03/01-" + "T",
                "T",
                250,
                dateFormat.parse("2023/03/01"),
                proveedor,
                quincena);
        AcopioLecheEntity acopioLeche3 = new AcopioLecheEntity(
                proveedor.getCodigo() +  "-2023/03/02-" + "M",
                "M",
                75,
                dateFormat.parse("2023/03/02"),
                proveedor,
                quincena);
        AcopioLecheEntity acopioLeche4 = new AcopioLecheEntity(
                proveedor.getCodigo() +  "-2023/03/03-" + "T",
                "T",
                300,
                dateFormat.parse("2023/03/03"),
                proveedor,
                quincena);
        ArrayList<AcopioLecheEntity> acopiosLeche = new ArrayList<>();
        acopiosLeche.add(acopioLeche1);
        acopiosLeche.add(acopioLeche2);
        acopiosLeche.add(acopioLeche3);
        acopiosLeche.add(acopioLeche4);
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity("12345-2023/03/1", 25, 32, proveedor, quincena);
        DatosCentroAcopioEntity datosCaAnterior = new DatosCentroAcopioEntity(
                proveedor.getCodigo() + "-2023/02/2",
                5,
                2,
                3,
                700,
                0,
                0,
                0,
                grasaSolidoTotal,
                proveedor,
                quincena.obtenerQuincenaAnterior()
        );

        when(proveedorServiceMock.obtenerProveedores()).thenReturn(new ArrayList<>(Arrays.asList(proveedor)));
        when(acopioLecheServiceMock.obtenerAcopiosLechePorProveedorQuincena(proveedor, quincena)).thenReturn(acopiosLeche);
        when(laboratorioLecheServiceMock.obtenerLaboratorioLechePorProveedorQuincena(proveedor, quincena)).thenReturn(grasaSolidoTotal);
        when(datosCentroAcopioRepositoryMock.findByProveedorAndQuincena(proveedor, quincena.obtenerQuincenaAnterior())).thenReturn(Optional.of(datosCaAnterior));

        List<DatosCentroAcopioEntity> listaDatosCa =  datosCentroAcopioService.calcularDatosCAPorQuincena(quincena);
        assertTrue(!listaDatosCa.isEmpty());
    }

    @Test
    void testCalcularDatosCAPorProveedorQuincenaKlsLeche0() {
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");

        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity("12345-2023/03/1", 0, 0, proveedor, quincena);
        DatosCentroAcopioEntity datosCaAnterior = new DatosCentroAcopioEntity(
                proveedor.getCodigo() + "-2023/02/2",
                5,
                2,
                3,
                700,
                0,
                0,
                0,
                grasaSolidoTotal,
                proveedor,
                quincena.obtenerQuincenaAnterior()
        );

        when(acopioLecheServiceMock.obtenerAcopiosLechePorProveedorQuincena(proveedor, quincena)).thenReturn(new ArrayList<>());
        when(laboratorioLecheServiceMock.obtenerLaboratorioLechePorProveedorQuincena(proveedor, quincena)).thenReturn(grasaSolidoTotal);
        when(datosCentroAcopioRepositoryMock.findByProveedorAndQuincena(proveedor, quincena.obtenerQuincenaAnterior())).thenReturn(Optional.of(datosCaAnterior));

        DatosCentroAcopioEntity datosCentroAcopio = datosCentroAcopioService.calcularDatosCAPorProveedorQuincena(proveedor, quincena);
        assertEquals(0, datosCentroAcopio.getTotalKlsLeche());
    }

    @Test
    //Test para verificar que se lance la excepcion de que existen datos para la quincena
    // anterior los cuales no fueron calculados.
    void testCalcularVariacionesDatosCADatosQuincenaAnteriorNoCalculados(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity("12345-2023/03/1", 25, 32, proveedor, quincena);
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity(
                proveedor.getCodigo() + "-2023/02/2",
                5,
                2,
                3,
                700,
                0,
                0,
                0,
                grasaSolidoTotal,
                proveedor,
                quincena
        );

        when(datosCentroAcopioRepositoryMock.findByProveedorAndQuincena(proveedor, quincena.obtenerQuincenaAnterior())).thenReturn(Optional.empty());
        when(acopioLecheServiceMock.existenAcopiosLechePorQuincena(quincena.obtenerQuincenaAnterior())).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            datosCentroAcopioService.calcularVariacionesDatosCA(datosCentroAcopio);
        });
        assertEquals("Existen pagos del centro de acopio no calculados para la quincena anterior", exception.getMessage());
    }

    @Test
    //Test para para verificar el caso en que no existen datos para la quincena anterior
    void testCalcularVariacionesDatosCANoExistenDatosQuincenaAnterior() {
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity("12345-2023/03/1", 25, 32, proveedor, quincena);
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity(
                proveedor.getCodigo() + "-2023/02/2",
                5,
                2,
                3,
                700,
                0,
                0,
                0,
                grasaSolidoTotal,
                proveedor,
                quincena
        );

        when(datosCentroAcopioRepositoryMock.findByProveedorAndQuincena(proveedor, quincena.obtenerQuincenaAnterior())).thenReturn(Optional.empty());
        when(acopioLecheServiceMock.existenAcopiosLechePorQuincena(quincena.obtenerQuincenaAnterior())).thenReturn(false);
        datosCentroAcopioService.calcularVariacionesDatosCA(datosCentroAcopio);
        assertEquals(0, datosCentroAcopio.getVariacionLeche());
        assertEquals(0, datosCentroAcopio.getVariacionGrasa());
        assertEquals(0, datosCentroAcopio.getVariacionSolidoTotal());
    }

    @Test
    //Test para la funcion de existencia de datos para el calculo de los datos
    // del centro de acopio para una quincena dada caso True
    void testExistenDatosCAParaCalculoPorQuincenaTrue(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);

        when(acopioLecheServiceMock.existenAcopiosLechePorQuincena(quincena)).thenReturn(true);
        when(laboratorioLecheServiceMock.existeLaboratorioLechePorQuincena(quincena)).thenReturn(true);

        assertTrue(datosCentroAcopioService.existenDatosCAParaCalculoPorQuincena(quincena));
    }

    @Test
    //Test para la funcion de existencia de datos para el calculo de los datos
    // del centro de acopio para una quincena dada caso False
    void testExistenDatosCAParaCalculoPorQuincenaFalse(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 03, 1);

        when(acopioLecheServiceMock.existenAcopiosLechePorQuincena(quincena)).thenReturn(true);
        when(laboratorioLecheServiceMock.existeLaboratorioLechePorQuincena(quincena)).thenReturn(false);

        assertFalse(datosCentroAcopioService.existenDatosCAParaCalculoPorQuincena(quincena));
    }
}
