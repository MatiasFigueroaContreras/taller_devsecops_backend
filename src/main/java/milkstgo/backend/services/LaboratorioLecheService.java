package milkstgo.backend.services;

import lombok.Generated;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import milkstgo.backend.entities.LaboratorioLecheEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;
import milkstgo.backend.repositories.LaboratorioLecheRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratorioLecheService {
    @Autowired
    LaboratorioLecheRepository laboratorioLecheRepository;
    @Autowired
    ProveedorService proveedorService;

    public void guardarDatosLaboratorioLeche(LaboratorioLecheEntity laboratorioLeche) {
        String codigoProveedor = laboratorioLeche.getProveedor().getCodigo();
        ProveedorEntity proveedor = proveedorService.obtenerProveedorPorCodigo(codigoProveedor);
        laboratorioLeche.setProveedor(proveedor);
        String quincena = laboratorioLeche.getQuincena().toString();
        String id = codigoProveedor + "-" + quincena;
        laboratorioLeche.setId(id);
        laboratorioLecheRepository.save(laboratorioLeche);
    }

    public void guardarListaDatosLaboratorioLeche(List<LaboratorioLecheEntity> laboratorioLecheEntityList, QuincenaEntity quincena) {
        for (LaboratorioLecheEntity laboratorioLeche : laboratorioLecheEntityList) {
            laboratorioLeche.setQuincena(quincena);
            guardarDatosLaboratorioLeche(laboratorioLeche);
        }
    }

    public void validarListaDatosLaboratorioLeche(List<LaboratorioLecheEntity> laboratorioLecheEntityList) {
        for (LaboratorioLecheEntity laboratorioLeche : laboratorioLecheEntityList) {
            validarDatosLaboratorioLeche(laboratorioLeche);
        }
    }

    public void validarDatosLaboratorioLeche(LaboratorioLecheEntity grasaSolidoTotal) {
        ProveedorEntity proveedor = grasaSolidoTotal.getProveedor();
        Integer porcentajeGrasa = grasaSolidoTotal.getPorcentajeGrasa();
        Integer porcentajeSolidoTotal = grasaSolidoTotal.getPorcentajeSolidoTotal();
        if (porcentajeGrasa < 0 || porcentajeGrasa > 100) {
            throw new IllegalArgumentException("El porcentaje de grasa no es valido");
        }

        if (porcentajeSolidoTotal < 0 || porcentajeSolidoTotal > 100) {
            throw new IllegalArgumentException("El porcentaje de solido total no es valido");
        }

        if (!proveedorService.existeProveedor(proveedor)) {
            throw new IllegalArgumentException("Los proveedores tienen que estar registrados");
        }
    }

    public LaboratorioLecheEntity obtenerLaboratorioLechePorProveedorQuincena(ProveedorEntity proveedor, QuincenaEntity quincena) {
        Optional<LaboratorioLecheEntity> laboratorioLeche =  laboratorioLecheRepository.findByProveedorAndQuincena(proveedor, quincena);
        if(laboratorioLeche.isEmpty()){
            throw new IllegalArgumentException("No existe datos de grasa y solido total para un proveedor dada la quincena ingresada");
        }

        return laboratorioLeche.get();
    }

    public boolean existeLaboratorioLechePorQuincena(QuincenaEntity quincena) {
        return laboratorioLecheRepository.existsByQuincena(quincena);
    }

    @Generated
    public List<LaboratorioLecheEntity> leerExcel(MultipartFile file) {
        List<LaboratorioLecheEntity> grasasSolidosTotales = new ArrayList<>();
        String filename = file.getOriginalFilename();

        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new IllegalArgumentException("El archivo ingresado no es un .xlsx");
        }
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException("El archivo ingresado no pudo ser leido");
        }
        XSSFSheet worksheet = workbook.getSheetAt(0);
        boolean rowVerification = true;
        for (Row row : worksheet) {
            if (rowVerification) {
                rowVerification = false;
                continue;
            }

            Iterator<Cell> cellItr = row.iterator();
            int iCell = 0;
            LaboratorioLecheEntity grasaSolidoTotal = new LaboratorioLecheEntity();
            ProveedorEntity proveedor = new ProveedorEntity();
            while (cellItr.hasNext()) {
                Cell cell = cellItr.next();
                setValueByCell(grasaSolidoTotal, proveedor, cell, iCell);
                iCell++;
            }
            if (iCell == 3) {
                grasaSolidoTotal.setProveedor(proveedor);
                grasasSolidosTotales.add(grasaSolidoTotal);
            }
        }

        return grasasSolidosTotales;
    }

    @Generated
    private void setValueByCell(LaboratorioLecheEntity grasaSolidoTotal, ProveedorEntity proveedor, Cell cell, int iCell) {
        try {
            switch (iCell) {
                case 0 -> proveedor.setCodigo(getCodigoValuByCell(cell));
                case 1 -> grasaSolidoTotal.setPorcentajeGrasa((int) cell.getNumericCellValue());
                case 2 -> grasaSolidoTotal.setPorcentajeSolidoTotal((int) cell.getNumericCellValue());
                default -> {
                    //No pasa por aca
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("El Excel ingresado contiene datos no validos.");
        }
    }

    @Generated
    private String getCodigoValuByCell(Cell cell){
        try {
            return cell.getStringCellValue();
        }
        catch (IllegalStateException e) {
            int codigo = (int) cell.getNumericCellValue();
            return Integer.toString(codigo);
        }
    }
}
