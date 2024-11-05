package milkstgo.backend;

import milkstgo.backend.repositories.QuincenaRepository;
import milkstgo.backend.services.QuincenaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import milkstgo.backend.entities.QuincenaEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class QuincenaServiceTests {
    @Mock
    private QuincenaRepository quincenaRepositoryMock;
    @InjectMocks
    private QuincenaService quincenaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    //Test para verificar cuando se ingresa una quincena valida
    void testIngresarQuincenaExitosa() {
        QuincenaEntity quincena = new QuincenaEntity();
        quincena.setYear(2023);
        quincena.setMes(3);
        quincena.setNumero(1);
        String id = quincena.toString();
        quincena.setId(id);

        when(quincenaRepositoryMock.save(any(QuincenaEntity.class))).thenReturn(quincena);

        QuincenaEntity resultado = quincenaService.ingresarQuincena(quincena.getYear(), quincena.getMes(), quincena.getNumero());
        assertEquals(quincena, resultado);
    }

    @Test
    //Test para verificar que se lance una excepcion al ingreso de quincena con un año no valido
    void testIngresarQuincenaYearInvalido() {
        Integer year = -1;
        Integer mes = 8;
        Integer numero = 1;

        Exception exception = assertThrows(Exception.class, () -> quincenaService.ingresarQuincena(year, mes, numero));
        assertEquals("El año ingresado no es valido", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al ingreso de quincena con un mes no valido
    void testIngresarQuincenaMesInvalido() {
        Integer year = 2023;
        Integer mes = 13;
        Integer numero = 1;

        Exception exception = assertThrows(Exception.class, () -> quincenaService.ingresarQuincena(year, mes, numero));
        assertEquals("El mes de la quincena no es valido", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion al ingreso de quincena con un numero no valido
    void testIngresarQuincenaNumeroInvalido() {
        Integer year = 2023;
        Integer mes = 12;
        Integer numero = 5;

        Exception exception = assertThrows(Exception.class, () -> quincenaService.ingresarQuincena(year, mes, numero));
        assertEquals("El numero de quincena no es valido", exception.getMessage());
    }

    @Test
    //Test para verificar que se lance una excepcion cuando la fecha es superior a la fecha actual
    void testIngresarQuincenaFechaSuperior() {
        LocalDateTime fechaActual = LocalDateTime.now();
        Integer year = fechaActual.getYear() + 1;
        Integer mes = 11;
        Integer numero = 2;

        Exception exception = assertThrows(Exception.class, () -> quincenaService.ingresarQuincena(year, mes, numero));
        assertEquals("La quincena ingresada es superior a la fecha actual", exception.getMessage());
    }

    @Test
    //Test para verificar cuando la quincena ya está registrada
    void testEstaRegistradaQuincenaTrue() {
        QuincenaEntity quincena = new QuincenaEntity();
        quincena.setYear(2023);
        quincena.setMes(3);
        quincena.setNumero(1);
        String id = quincena.toString();
        quincena.setId(id);

        when(quincenaRepositoryMock.findById(any(String.class))).thenReturn(Optional.of(quincena));

        boolean resultado = quincenaService.estaRegistradaQuincena(quincena.getYear(), quincena.getMes(), quincena.getNumero());
        assertTrue(resultado);
    }

    @Test
    //Test para verificar cuando la quincena no está registrada
    void testEstaRegistradaQuincenaFalse() {
        QuincenaEntity quincena = new QuincenaEntity();
        quincena.setYear(2023);
        quincena.setMes(3);
        quincena.setNumero(2);
        String id = quincena.toString();
        quincena.setId(id);

        when(quincenaRepositoryMock.findById(any(String.class))).thenReturn(Optional.empty());

        boolean resultado = quincenaService.estaRegistradaQuincena(2023, 1, 1);
        assertFalse(resultado);
    }
}
