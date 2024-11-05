package milkstgo.backend.controllers;

import milkstgo.backend.exceptions.AlreadyExistsException;
import milkstgo.backend.services.LaboratorioLecheService;
import milkstgo.backend.services.PagoService;
import milkstgo.backend.services.QuincenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import milkstgo.backend.entities.LaboratorioLecheEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.List;

@RestController
@RequestMapping("/laboratorio-leche")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LaboratorioLecheController {
    @Autowired
    LaboratorioLecheService laboratorioLecheService;
    @Autowired
    QuincenaService quincenaService;
    @Autowired
    PagoService pagoService;

    @PostMapping("/importar")
    public ResponseEntity<String> importarLaboratorioLeche(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("year") Integer year,
                                                     @RequestParam("mes") Integer mes,
                                                     @RequestParam("quincena") Integer numero) {
        QuincenaEntity quincena = quincenaService.ingresarQuincena(year, mes, numero);
        if(pagoService.existenPagosPorQuincena(quincena)){
            throw new AlreadyExistsException("Ya existen datos calculados para la quincena seleccionada");
        }

        List<LaboratorioLecheEntity> grasasSolidosTotales = laboratorioLecheService.leerExcel(file);
        laboratorioLecheService.validarListaDatosLaboratorioLeche(grasasSolidosTotales);
        laboratorioLecheService.guardarListaDatosLaboratorioLeche(grasasSolidosTotales, quincena);
        return ResponseEntity.ok("Datos registrados correctamente!");
    }
}
