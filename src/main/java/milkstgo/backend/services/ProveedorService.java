package milkstgo.backend.services;

import milkstgo.backend.exceptions.AlreadyExistsException;
import milkstgo.backend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.repositories.ProveedorRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        proveedor.setIsDeleted(false);
        return proveedorRepository.save(proveedor);
    }

    public ProveedorEntity actualizarProveedor(Long id, String codigo, String nombre, String categoria, String retencion) {
        ProveedorEntity proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El proveedor no se encuentra registrado"));

        if(proveedor.getIsDeleted().booleanValue()) {
            throw new NotFoundException("El proveedor se encuentra eliminado");
        }

        if(!codigo.equals(proveedor.getCodigo()) && proveedorRepository.existsByCodigo(codigo)) {
            throw new AlreadyExistsException("Ya se encuentra registrado un proveedor con el codigo otorgado");
        }
        validarCodigo(codigo);
        validarCategoria(categoria);
        validarRetencion(retencion);

        proveedor.setCodigo(codigo);
        proveedor.setNombre(nombre);
        proveedor.setCategoria(categoria);
        proveedor.setRetencion(retencion);
        return proveedorRepository.save(proveedor);
    }

    public void validarDatosProveedor(String codigo, String categoria, String retencion){
        validarCodigo(codigo);
        validarCategoria(categoria);
        validarRetencion(retencion);

        if (proveedorRepository.existsByCodigoAndIsDeletedFalse(codigo)) {
            throw new IllegalArgumentException("El proveedor ya se encuentra registrado");
        }
    }

    public void validarCodigo(String codigo) {
        //Verificacion de un codigo correcto (5 digitos numericos)

        try {
            Integer codigoInt = Integer.parseInt(codigo);
            if (codigo.length() != 5 || codigoInt < 0) {
                throw new IllegalArgumentException("El codigo tiene que ser de 5 digitos numericos");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El codigo tiene que ser de 5 digitos numericos");
        }
    }

    public void validarCategoria(String categoria) {
        //Verificacion de categorias validas establecidas
        if (!Arrays.asList(CATEGORIAS_VALIDAS).contains(categoria)) {
            throw new IllegalArgumentException("La categoria ingresada no es valida");
        }
    }

    public void validarRetencion(String retencion) {
        //Verificacion de valores validos para retencion
        if (!retencion.equals("Si") && !retencion.equals("No")) {
            throw new IllegalArgumentException("El afecto a retencion ingresado no es valido");
        }
    }

    public List<ProveedorEntity> obtenerProveedores() {
        return new ArrayList<>(proveedorRepository.findAllByIsDeletedFalse());
    }

    public boolean existeProveedor(ProveedorEntity proveedor) {
        return proveedorRepository.existsByCodigoAndIsDeletedFalse(proveedor.getCodigo());
    }

    public ProveedorEntity obtenerProveedorPorCodigo(String codigo) {
        return proveedorRepository.findByCodigoAndIsDeletedFalse(codigo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el proveedor con el codigo"));
    }

    public void eliminarProveedor(Long id) {
        ProveedorEntity proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El proveedor no se encuentra registrado"));

        proveedor.setIsDeleted(true);
        proveedorRepository.save(proveedor);
    }
}
