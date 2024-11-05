package milkstgo.backend.services;

import lombok.Generated;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import milkstgo.backend.entities.AcopioLecheEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;
import milkstgo.backend.repositories.AcopioLecheRepository;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class AcopioLecheService {
    @Autowired
    AcopioLecheRepository acopioLecheRepository;
    @Autowired
    ProveedorService proveedorService;

    public void guardarAcopioLeche(AcopioLecheEntity acopioLeche) {
        String codigoProveedor = acopioLeche.getProveedor().getCodigo();
        ProveedorEntity proveedor = proveedorService.obtenerProveedorPorCodigo(codigoProveedor);
        acopioLeche.setProveedor(proveedor);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String fecha = dateFormat.format(acopioLeche.getFecha());
        String turno = acopioLeche.getTurno();
        String id = codigoProveedor + "-" + fecha + "-" + turno;
        acopioLeche.setId(id);
        acopioLecheRepository.save(acopioLeche);
    }

    public void guardarAcopiosLeches(List<AcopioLecheEntity> acopiosLeche) {
        for (AcopioLecheEntity acopioLeche : acopiosLeche) {
            guardarAcopioLeche(acopioLeche);
        }
    }

    public List<AcopioLecheEntity> obtenerAcopiosLechePorProveedorQuincena(ProveedorEntity proveedor, QuincenaEntity quincena) {
        return acopioLecheRepository.findAllByProveedorAndQuincena(proveedor, quincena);
    }

    public boolean existenAcopiosLechePorQuincena(QuincenaEntity quincena) {
        return acopioLecheRepository.existsByQuincena(quincena);
    }

    public void validarListaAcopioLecheQuincena(List<AcopioLecheEntity> acopiosLeche, QuincenaEntity quincena) {
        for (AcopioLecheEntity acopioLeche : acopiosLeche) {
            acopioLeche.setQuincena(quincena);
            validarAcopioLeche(acopioLeche);
        }
    }

    public void validarAcopioLeche(AcopioLecheEntity acopioLeche) {
        String turno = acopioLeche.getTurno();
        Integer klsLeche = acopioLeche.getCantidadLeche();
        Date fecha = acopioLeche.getFecha();
        ProveedorEntity proveedor = acopioLeche.getProveedor();
        QuincenaEntity quincena = acopioLeche.getQuincena();
        if (!turno.equals("M") && !turno.equals("T")) {
            throw new IllegalArgumentException("Algun turno no es valido, debe ser M o T");
        }

        if (klsLeche < 0) {
            throw new IllegalArgumentException("Los kilos de leche tienen que ser positivos");
        }

        if (!quincena.estaDentroQuincena(fecha)) {
            throw new IllegalArgumentException("Las fechas ingresadas tienen que coincidir con la quincena");
        }

        if (!proveedorService.existeProveedor(proveedor)) {
            throw new IllegalArgumentException("Los proveedores tienen que estar registrados");
        }
    }

    @Generated
    public List<AcopioLecheEntity> leerExcel(MultipartFile file) {
        List<AcopioLecheEntity> acopiosLeche = new ArrayList<>();
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".xlsx")) {
            throw new IllegalArgumentException("El archivo ingresado no es un .xlsx");
        }

        XSSFWorkbook workBook;
        try {
            workBook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException("El archivo ingresado no pudo ser leido");
        }

        XSSFSheet workSheet = workBook.getSheetAt(0);
        boolean rowVerification = true;

        for (Row row : workSheet) {
            if (rowVerification) {
                rowVerification = false;
                continue;
            }

            Iterator<Cell> cellIterator = row.iterator();
            int iCell = 0;
            AcopioLecheEntity acopioLeche = new AcopioLecheEntity();
            ProveedorEntity proveedor = new ProveedorEntity();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                setValoresPorCelda(acopioLeche, proveedor, cell, iCell);
                iCell++;
            }
            if (iCell == 4) {
                acopioLeche.setProveedor(proveedor);
                acopiosLeche.add(acopioLeche);
            }
        }

        return acopiosLeche;
    }

    @Generated
    private void setValoresPorCelda(AcopioLecheEntity acopioLeche, ProveedorEntity proveedor, Cell cell, int iCell) {
        try {
            switch (iCell) {
                case 0 -> acopioLeche.setFecha(cell.getDateCellValue());
                case 1 -> acopioLeche.setTurno(cell.getStringCellValue());
                case 2 -> proveedor.setCodigo(getCodigoPorCelda(cell));
                case 3 -> acopioLeche.setCantidadLeche((int) cell.getNumericCellValue());
                default -> {
                    //No pasa por aqui
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("El Excel ingresado contiene datos no validos");
        }
    }

    @Generated
    private String getCodigoPorCelda(Cell cell){
        try {
            return cell.getStringCellValue();
        }
        catch (IllegalStateException e) {
            int codigo = (int) cell.getNumericCellValue();
            return Integer.toString(codigo);
        }
    }
}
