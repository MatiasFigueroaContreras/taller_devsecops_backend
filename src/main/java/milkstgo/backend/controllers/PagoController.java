package milkstgo.backend.controllers;

import milkstgo.backend.services.DatosCentroAcopioService;
import milkstgo.backend.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import milkstgo.backend.entities.DatosCentroAcopioEntity;
import milkstgo.backend.entities.PagoEntity;
import milkstgo.backend.entities.QuincenaEntity;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PagoController {
    @Autowired
    PagoService pagoService;
    @Autowired
    DatosCentroAcopioService datosCentroAcopioService;

    @PostMapping("/calcular")
    public ResponseEntity<String> calcularPlanillaPagos(@RequestParam("year") Integer year,
                                                       @RequestParam("mes") Integer mes,
                                                       @RequestParam("quincena") Integer numero,
                                                       RedirectAttributes redirectAttr) {
        QuincenaEntity quincena = new QuincenaEntity(year, mes, numero);

        if(pagoService.existenPagosPorQuincena(quincena)){
            return ResponseEntity.ok("Ya existen pagos calculados");
        }
        else if(datosCentroAcopioService.existenDatosCAParaCalculoPorQuincena(quincena)){
            try {
                List<DatosCentroAcopioEntity> listaDatosCa = datosCentroAcopioService.calcularDatosCAPorQuincena(quincena);
                datosCentroAcopioService.guardarListaDatosCA(listaDatosCa);
                List<PagoEntity> pagos = pagoService.calcularPagos(listaDatosCa);
                pagoService.guardarPagos(pagos);
                return ResponseEntity.ok("Planilla de pagos calculada!");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        else{
            return ResponseEntity.badRequest().body("No se han ingresado los datos del centro de acopio para el calculo de los pagos");
        }
    }

    @GetMapping("/byquincena")
    public ResponseEntity<List<PagoEntity>> getAllByQuincena(@RequestParam("year") Integer year,
                                                             @RequestParam("mes") Integer mes,
                                                             @RequestParam("quincena") Integer numero) {
        QuincenaEntity quincena = new QuincenaEntity(year, mes, numero);
        List<PagoEntity> pagos = pagoService.obtenerPagosPorQuincena(quincena);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping
    public ResponseEntity<List<PagoEntity>> getAll(){
        List<PagoEntity> pagos = pagoService.obtenerPagos();
        return ResponseEntity.ok(pagos);
    }
}
