package milkstgo.backend;

import milkstgo.backend.entities.*;
import milkstgo.backend.repositories.PagoRepository;
import milkstgo.backend.services.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class PagoServiceTests {
    @Mock
    PagoRepository pagoRepositoryMock;
    @InjectMocks
    PagoService pagoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerPagos(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 3, 1);
        PagoEntity pago1 = new PagoEntity("12345-2023/03/1", 500000, 12000, 3500, 20000, 150, 0, 0, 535350, 0, 535350, proveedor, quincena, new DatosCentroAcopioEntity());
        PagoEntity pago2 = new PagoEntity("12345-2023/02/2", 500000, 12000, 3500, 20000, 150, 0, 0, 535350, 0, 535350, proveedor, quincena.obtenerQuincenaAnterior(), new DatosCentroAcopioEntity());
        ArrayList<PagoEntity> pagos = new ArrayList<>();
        pagos.add(pago1);
        pagos.add(pago2);
        when(pagoRepositoryMock.findAllByOrderByQuincenaDescProveedorCodigoAsc()).thenReturn(pagos);

        List<PagoEntity> respuesta = pagoService.obtenerPagos();
        assertEquals(respuesta, pagos);
    }

    @Test
    void testObtenerPagosPorQuincena(){
        ProveedorEntity proveedor1 = new ProveedorEntity("12345", "Proveedor 1", "A", "Si");
        ProveedorEntity proveedor2 = new ProveedorEntity("54321", "Proveedor 2", "C", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 3, 1);
        PagoEntity pago1 = new PagoEntity("12345-2023/03/1", 500000, 12000, 3500, 20000, 150, 0, 0, 535350, 0, 535350, proveedor1, quincena, new DatosCentroAcopioEntity());
        PagoEntity pago2 = new PagoEntity("54321-2023/03/1", 500000, 12000, 3500, 20000, 150, 0, 0, 535350, 0, 535350, proveedor2, quincena, new DatosCentroAcopioEntity());
        ArrayList<PagoEntity> pagos = new ArrayList<>();
        pagos.add(pago1);
        pagos.add(pago2);

        when(pagoRepositoryMock.findAllByQuincena(quincena)).thenReturn(pagos);

        List<PagoEntity> respuesta = pagoService.obtenerPagosPorQuincena(quincena);
        assertEquals(respuesta, pagos);
    }

    @Test
    void testExistenagosPorQuincena(){
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 3, 1);

        when(pagoRepositoryMock.existsByQuincena(quincena)).thenReturn(true);

        boolean respuesta = pagoService.existenPagosPorQuincena(quincena);

        assertTrue(respuesta);
    }

    @Test
    void testGuardarPagos(){
        ProveedorEntity proveedor1 = new ProveedorEntity("12345", "Proveedor 1", "A", "Si");
        ProveedorEntity proveedor2 = new ProveedorEntity("54321", "Proveedor 2", "C", "Si");
        QuincenaEntity quincena = new QuincenaEntity("2023/03/1", 2023, 3, 1);
        PagoEntity pago1 = new PagoEntity("", 500000, 12000, 3500, 20000, 150, 0, 0, 535350, 0, 535350, proveedor1, quincena, new DatosCentroAcopioEntity());
        PagoEntity pago2 = new PagoEntity("", 500000, 12000, 3500, 20000, 150, 0, 0, 535350, 0, 535350, proveedor2, quincena, new DatosCentroAcopioEntity());
        ArrayList<PagoEntity> pagos = new ArrayList<>();
        pagos.add(pago1);
        pagos.add(pago2);

        pagoService.guardarPagos(pagos);
        verify(pagoRepositoryMock, times(1)).save(pago1);
        verify(pagoRepositoryMock, times(1)).save(pago2);
    }

    @Test
    void testCalcularPagos(){
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

        ArrayList<DatosCentroAcopioEntity> listaDatosCa = new ArrayList<>();
        listaDatosCa.add(datosCentroAcopio);

        List<PagoEntity> respuesta = pagoService.calcularPagos(listaDatosCa);
        assertTrue(!respuesta.isEmpty());
    }


    @ParameterizedTest
    @CsvSource({"A, 700", "B, 550", "C, 400", "D, 250"})
    void testCalcularPagoLecheCategorias(String categoria, int pago){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", categoria, "Si");
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        Integer klsTotal = 1000;
        datosCentroAcopio.setTotalKlsLeche(klsTotal);
        datosCentroAcopio.setProveedor(proveedor);

        Integer pago_leche = pagoService.calcularPagoLeche(datosCentroAcopio);

        assertEquals(klsTotal * pago, pago_leche);
    }

    @ParameterizedTest
    @CsvSource({"10, 1000, 30000", "30, 500, 40000", "50, 1500, 180000"})
    void testCalcularPagoGrasa(int porcentajeGrasa, int totalKlsLeche, int resultadoEsperado){
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity();
        grasaSolidoTotal.setPorcentajeGrasa(porcentajeGrasa);
        datosCentroAcopio.setLaboratorioLeche(grasaSolidoTotal);
        datosCentroAcopio.setTotalKlsLeche(totalKlsLeche);

        Integer pagoGrasa = pagoService.calcularPagoGrasa(datosCentroAcopio);

        assertEquals(resultadoEsperado, pagoGrasa);
    }

    @ParameterizedTest
    @CsvSource({"5, -13000", "15, -9000", "25, 9500", "40, 15000"})
    void testCalcularPagoSolidoTotal(int porcentajeSolidoTotal, int resultadoEsperado){
        LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity();
        grasaSolidoTotal.setPorcentajeSolidoTotal(porcentajeSolidoTotal);
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setLaboratorioLeche(grasaSolidoTotal);
        datosCentroAcopio.setTotalKlsLeche(100);
        Integer pagoSolidoTotal = pagoService.calcularPagoSolidoTotal(datosCentroAcopio);
        assertEquals(resultadoEsperado, pagoSolidoTotal);
    }

    @Test
    void testCalcularBonificacionMayorA10DiasEnvioMT() {
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setDiasEnvioMyT(15);
        Integer pagoLeche = 1000;
        Integer bonificacion = pagoService.calcularBonificacionPorFrecuencia(datosCentroAcopio, pagoLeche);
        assertEquals(200, bonificacion);
    }

    @Test
    void testCalcularBonificacionMayorA10DiasEnvioM() {
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setDiasEnvioMyT(5);
        datosCentroAcopio.setDiasEnvioM(12);
        Integer pagoLeche = 1000;
        Integer bonificacion = pagoService.calcularBonificacionPorFrecuencia(datosCentroAcopio, pagoLeche);
        assertEquals(120, bonificacion);
    }

    @Test
    void testCalcularBonificacionMayorA10DiasEnvioT() {
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setDiasEnvioMyT(5);
        datosCentroAcopio.setDiasEnvioM(7);
        datosCentroAcopio.setDiasEnvioT(11);
        Integer pagoLeche = 1000;
        Integer bonificacion = pagoService.calcularBonificacionPorFrecuencia(datosCentroAcopio, pagoLeche);
        assertEquals( 80, bonificacion);
    }

    @Test
    void testCalcularBonificacionSinBonificacion() {
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setDiasEnvioMyT(5);
        datosCentroAcopio.setDiasEnvioM(7);
        datosCentroAcopio.setDiasEnvioT(9);
        Integer pagoLeche = 1000;
        Integer bonificacion = pagoService.calcularBonificacionPorFrecuencia(datosCentroAcopio, pagoLeche);
        assertEquals(0, bonificacion);
    }

    @ParameterizedTest
    @CsvSource({"-15, 70", "-35, 150", "-60, 300"})
    void testCalcularDctoVariacionLeche(int variacionLeche, int resultadoEsperado){
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setVariacionLeche(variacionLeche);
        Integer pagoAcopioLeche = 1000;
        Integer dctoVariacionLeche = pagoService.calcularDctoVariacionLeche(datosCentroAcopio, pagoAcopioLeche);
        assertEquals(resultadoEsperado, dctoVariacionLeche);
    }

    @ParameterizedTest
    @CsvSource({"-20, 1200", "-35, 2000", "-50, 3000"})
    void testCalcularDctoVariaiconGrasa(int variacionGrasa, int resultadoEsperado){
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setVariacionGrasa(variacionGrasa);
        Integer pagoAcopioLeche = 10000;
        Integer dctoVariacionGrasa = pagoService.calcularDctoVariacionGrasa(datosCentroAcopio, pagoAcopioLeche);

        assertEquals(resultadoEsperado, dctoVariacionGrasa);
    }

    @ParameterizedTest
    @CsvSource({"-8, 180", "-18, 270", "-40, 450"})
    void testCalcularDctoVariaiconSolidoTotal(int variacionSolidoTotal, int resultadoEsperado){
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setVariacionSolidoTotal(variacionSolidoTotal);
        Integer pagoAcopioLeche = 1000;
        Integer dctoVariacionSolidoTotal = pagoService.calcularDctoVariacionSolidoTotal(datosCentroAcopio, pagoAcopioLeche);

        assertEquals(resultadoEsperado, dctoVariacionSolidoTotal);
    }

    @Test
    void testCalcularMontoRetencionPagaRetencion(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "Si");
        PagoEntity pago = new PagoEntity();
        pago.setPagoTotal(1000000);
        pago.setProveedor(proveedor);
        Integer montoRetencion = pagoService.calcularMontoRetencion(pago);

        assertEquals(130000, montoRetencion);
    }

    @Test
    void testCalcularMontoRetencionNoPagaRetencion(){
        ProveedorEntity proveedor = new ProveedorEntity("12345", "Proveedor", "A", "No");
        PagoEntity pago = new PagoEntity();
        pago.setPagoTotal(1000000);
        pago.setProveedor(proveedor);
        Integer montoRetencion = pagoService.calcularMontoRetencion(pago);

        assertEquals(0, montoRetencion);
    }
}
