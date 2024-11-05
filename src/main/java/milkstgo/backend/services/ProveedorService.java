package milkstgo.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.repositories.ProveedorRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProveedorService {
    @Autowired
    ProveedorRepository proveedorRepository;
    private final String[] CATEGORIAS_VALIDAS = {"A", "B", "C", "D"};

    public ProveedorEntity  registrarProveedor(String codigo, String nombre, String categoria, String retencion) {
        validarDatosProveedor(codigo, categoria, retencion);
        ProveedorEntity proveedor = new ProveedorEntity();
        proveedor.setCodigo(codigo);
        proveedor.setNombre(nombre);
        proveedor.setCategoria(categoria);
        proveedor.setRetencion(retencion);
        return proveedorRepository.save(proveedor);
    }

    public void validarDatosProveedor(String codigo, String categoria, String retencion){
        //Verificacion de un codigo correcto (5 digitos numericos)
        try {
            Integer codigoInt = Integer.parseInt(codigo);
            if (codigo.length() != 5 || codigoInt < 0) {
                throw new NumberFormatException("El codigo tiene que ser de 5 digitos numericos");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("El codigo tiene que ser de 5 digitos numericos");
        }
        //Verificacion de categorias validas establecidas
        if (!Arrays.asList(CATEGORIAS_VALIDAS).contains(categoria)) {
            throw new IllegalArgumentException("La categoria ingresada no es valida");
        }
        //Verificacion de valores validos para retencion
        if (!retencion.equals("Si") && !retencion.equals("No")) {
            throw new IllegalArgumentException("El afecto a retencion ingresado no es valido");
        }

        if (proveedorRepository.findById(codigo).isPresent()) {
            throw new IllegalArgumentException("El proveedor ya se encuentra registrado");
        }
    }

    public List<ProveedorEntity> obtenerProveedores() {
        return new ArrayList<>(proveedorRepository.findAll());
    }

    public boolean existeProveedor(ProveedorEntity proveedor) {
        return proveedorRepository.existsById(proveedor.getCodigo());
    }
}
