package milkstgo.backend.services;

import milkstgo.backend.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import milkstgo.backend.repositories.DatosCentroAcopioRepository;

import java.util.*;

@Service
public class DatosCentroAcopioService {
    @Autowired
    DatosCentroAcopioRepository datosCentroAcopioRepository;
    @Autowired
    AcopioLecheService acopioLecheService;
    @Autowired
    LaboratorioLecheService laboratorioLecheService;
    @Autowired
    ProveedorService proveedorService;

    public void guardarDatosCA(DatosCentroAcopioEntity datosCentroAcopio) {
        String id = datosCentroAcopio.getProveedor().getCodigo() + "-" + datosCentroAcopio.getQuincena().toString();
        datosCentroAcopio.setId(id);
        datosCentroAcopioRepository.save(datosCentroAcopio);
    }

    public void guardarListaDatosCA(List<DatosCentroAcopioEntity> listaDatosCa) {
        for (DatosCentroAcopioEntity datosCa : listaDatosCa) {
            guardarDatosCA(datosCa);
        }
    }

    public DatosCentroAcopioEntity obtenerDatosCAPorProveedorQuincena(ProveedorEntity proveedor, QuincenaEntity quincena) {
        Optional<DatosCentroAcopioEntity> datosCa = datosCentroAcopioRepository.findByProveedorAndQuincena(proveedor, quincena);
        if (!datosCa.isPresent()) {
            throw new IllegalArgumentException("No se encontraron los datos del centro de acopio, para el proveedor y quincena dados");
        }
        return datosCa.get();
    }

    public boolean existenDatosCAParaCalculoPorQuincena(QuincenaEntity quincena) {
        return acopioLecheService.existenAcopiosLechePorQuincena(quincena) &&
                laboratorioLecheService.existeLaboratorioLechePorQuincena(quincena);
    }

    public DatosCentroAcopioEntity calcularDatosCAPorProveedorQuincena(ProveedorEntity proveedor, QuincenaEntity quincena) {
        DatosCentroAcopioEntity datosCentroAcopio = new DatosCentroAcopioEntity();
        datosCentroAcopio.setProveedor(proveedor);
        datosCentroAcopio.setQuincena(quincena);
        calcularDatosAcopioLeche(datosCentroAcopio);
        LaboratorioLecheEntity grasaSolidoTotal;
        if(datosCentroAcopio.getTotalKlsLeche() == 0){
            //Se asigna 0 porcentajes si no han entregado datos para algun proveedor, considerando que este no envio leche.
            laboratorioLecheService.guardarDatosLaboratorioLeche(new LaboratorioLecheEntity("", 0, 0, proveedor, quincena));
        }
        grasaSolidoTotal = laboratorioLecheService.obtenerLaboratorioLechePorProveedorQuincena(proveedor, quincena);
        datosCentroAcopio.setLaboratorioLeche(grasaSolidoTotal);
        calcularVariacionesDatosCA(datosCentroAcopio);
        return datosCentroAcopio;
    }

    public List<DatosCentroAcopioEntity> calcularDatosCAPorQuincena(QuincenaEntity quincena) {
        List<ProveedorEntity> proveedores = proveedorService.obtenerProveedores();
        List<DatosCentroAcopioEntity> listaDatosCa = new ArrayList<>();
        for (ProveedorEntity proveedor : proveedores) {
            DatosCentroAcopioEntity datosCaProveedor = calcularDatosCAPorProveedorQuincena(proveedor, quincena);
            listaDatosCa.add(datosCaProveedor);
        }

        return listaDatosCa;
    }

    public void calcularDatosAcopioLeche(DatosCentroAcopioEntity datosCentroAcopio) {
        List<AcopioLecheEntity> acopiosLeche = acopioLecheService.obtenerAcopiosLechePorProveedorQuincena(datosCentroAcopio.getProveedor(), datosCentroAcopio.getQuincena());
        Integer nDiasEnvioMT = 0;
        Integer nDiasEnvioM = 0;
        Integer nDiasEnvioT;
        Integer totalKlsLeche = 0;
        HashMap<Integer, Boolean> diasEnviosM = new HashMap<>();
        HashMap<Integer, Boolean> diasEnviosT = new HashMap<>();
        for (AcopioLecheEntity acopioLeche : acopiosLeche) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(acopioLeche.getFecha());
            Integer dia = calendar.get(Calendar.DAY_OF_MONTH);
            String turno = acopioLeche.getTurno();
            if (turno.equals("M")) {
                diasEnviosM.put(dia, Boolean.TRUE);
            } else {
                diasEnviosT.put(dia, Boolean.TRUE);
            }
            totalKlsLeche += acopioLeche.getCantidadLeche();
        }

        for (Integer diaEnvioM : diasEnviosM.keySet()) {
            if (diasEnviosT.containsKey(diaEnvioM)) {
                nDiasEnvioMT++;
            } else {
                nDiasEnvioM++;
            }
        }
        nDiasEnvioT = diasEnviosT.size() - nDiasEnvioMT;
        datosCentroAcopio.setDiasEnvioMyT(nDiasEnvioMT);
        datosCentroAcopio.setDiasEnvioM(nDiasEnvioM);
        datosCentroAcopio.setDiasEnvioT(nDiasEnvioT);
        datosCentroAcopio.setTotalKlsLeche(totalKlsLeche);
    }

    public void calcularVariacionesDatosCA(DatosCentroAcopioEntity datosCentroAcopio) {
        Integer variacionLeche = 0;
        Integer variacionGrasa = 0;
        Integer variacionSolidoTotal = 0;
        LaboratorioLecheEntity grasaSolidoTotal = datosCentroAcopio.getLaboratorioLeche();
        QuincenaEntity quincenaAnterior = datosCentroAcopio.getQuincena().obtenerQuincenaAnterior();
        try {
            DatosCentroAcopioEntity datosCaAnterior = obtenerDatosCAPorProveedorQuincena(datosCentroAcopio.getProveedor(), quincenaAnterior);
            LaboratorioLecheEntity grasaStAnterior = datosCaAnterior.getLaboratorioLeche();
            if(datosCaAnterior.getTotalKlsLeche() != 0){
                variacionLeche = (datosCentroAcopio.getTotalKlsLeche() / datosCaAnterior.getTotalKlsLeche() - 1) * 100;
            }
            if(grasaStAnterior.getPorcentajeGrasa() != 0){
                variacionGrasa = (grasaSolidoTotal.getPorcentajeGrasa() / grasaStAnterior.getPorcentajeGrasa() - 1) * 100;
            }
            if(grasaStAnterior.getPorcentajeSolidoTotal() != 0){
                variacionSolidoTotal = (grasaSolidoTotal.getPorcentajeSolidoTotal() / grasaStAnterior.getPorcentajeSolidoTotal() - 1) * 100;
            }
        } catch (Exception e) {
            //No existen datos del centro acopio anteriormente.
            if (acopioLecheService.existenAcopiosLechePorQuincena(quincenaAnterior) ||
                    laboratorioLecheService.existeLaboratorioLechePorQuincena(quincenaAnterior)) {
                throw new IllegalArgumentException("Existen pagos del centro de acopio no calculados para la quincena anterior");
            }
        }
        datosCentroAcopio.setVariacionLeche(variacionLeche);
        datosCentroAcopio.setVariacionGrasa(variacionGrasa);
        datosCentroAcopio.setVariacionSolidoTotal(variacionSolidoTotal);
    }
}
