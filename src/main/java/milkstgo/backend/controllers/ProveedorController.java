package milkstgo.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.services.ProveedorService;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProveedorController {
    @Autowired
    ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<ProveedorEntity> create(@RequestParam("codigo") String codigo,
                                 @RequestParam("nombre") String nombre,
                                 @RequestParam("categoria") String categoria,
                                 @RequestParam("retencion") String retencion) {
        ProveedorEntity proveedor = proveedorService.registrarProveedor(codigo, nombre, categoria, retencion);
        return ResponseEntity.ok(proveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorEntity> update(@PathVariable("id") Long id,
                                                  @RequestParam("codigo") String codigo,
                                                  @RequestParam("nombre") String nombre,
                                                  @RequestParam("categoria") String categoria,
                                                  @RequestParam("retencion") String retencion) {
        ProveedorEntity proveedor = proveedorService.actualizarProveedor(id, codigo, nombre, categoria, retencion);
        return ResponseEntity.ok(proveedor);
    }

    @GetMapping
    public ResponseEntity<List<ProveedorEntity>> getAll() {
        List<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        if(proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(proveedores);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.ok("");
    }
}
