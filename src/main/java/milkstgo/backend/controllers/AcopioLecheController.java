package milkstgo.backend.controllers;

import milkstgo.backend.services.AcopioLecheService;
import milkstgo.backend.services.PagoService;
import milkstgo.backend.services.QuincenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import milkstgo.backend.entities.AcopioLecheEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.List;

@RestController
@RequestMapping("/acopios-leche")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AcopioLecheController {
    @Autowired
    AcopioLecheService acopioLecheService;
    @Autowired
    QuincenaService quincenaService;
    @Autowired
    PagoService pagoService;

    @PostMapping("/importar")
    public ResponseEntity<String> importarAcopioLeche(@RequestParam("file")MultipartFile file,
                                                      @RequestParam("year") Integer year,
                                                      @RequestParam("mes") Integer mes,
                                                      @RequestParam("quincena") Integer numero,
                                                      RedirectAttributes redirectAttr) {
        QuincenaEntity quincena = quincenaService.ingresarQuincena(year, mes, numero);
        if(pagoService.existenPagosPorQuincena(quincena)){
            return ResponseEntity.badRequest().body("Ya existen datos calculados para la quincena seleccionada");
        }

        try {
            List<AcopioLecheEntity> acopiosLeche = acopioLecheService.leerExcel(file);
            acopioLecheService.validarListaAcopioLecheQuincena(acopiosLeche, quincena);
            acopioLecheService.guardarAcopiosLeches(acopiosLeche);
            return ResponseEntity.ok("Datos registrados correctamente!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
