package milkstgo.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import milkstgo.backend.entities.QuincenaEntity;
import milkstgo.backend.repositories.QuincenaRepository;

import java.time.LocalDateTime;


@Service
public class QuincenaService {
    @Autowired
    QuincenaRepository quincenaRepository;

    public QuincenaEntity ingresarQuincena(Integer year, Integer mes, Integer numero) {
        validarDatosQuincena(year, mes, numero);

        QuincenaEntity quincena = new QuincenaEntity();
        quincena.setYear(year);
        quincena.setMes(mes);
        quincena.setNumero(numero);
        String id = quincena.toString();
        quincena.setId(id);
        return quincenaRepository.save(quincena);
    }

    public void validarDatosQuincena(Integer year, Integer mes, Integer numero) {
        LocalDateTime fechaActual = LocalDateTime.now();
        if (year < 0) {
            throw new IllegalArgumentException("El año ingresado no es valido");
        }

        if (mes > 12 || mes < 1) {
            throw new IllegalArgumentException("El mes de la quincena no es valido");
        }

        if (numero != 1 && numero != 2) {
            throw new IllegalArgumentException("El numero de quincena no es valido");
        }

        //Verifica que la quincena este antes de la fecha actual con 2 dias de margen
        if (fechaActual.isBefore(LocalDateTime.of(year, mes, 13 * numero, 0, 0))) {
            throw new IllegalArgumentException("La quincena ingresada es superior a la fecha actual");
        }
    }

    public boolean estaRegistradaQuincena(Integer year, Integer mes, Integer numero) {
        String id = year.toString() + "/" + mes.toString() + "-" + numero.toString();
        return quincenaRepository.findById(id).isPresent();
    }
}
