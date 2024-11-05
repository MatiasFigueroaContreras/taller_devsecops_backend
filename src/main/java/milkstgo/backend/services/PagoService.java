package milkstgo.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import milkstgo.backend.entities.DatosCentroAcopioEntity;
import milkstgo.backend.entities.PagoEntity;
import milkstgo.backend.entities.ProveedorEntity;
import milkstgo.backend.entities.QuincenaEntity;
import milkstgo.backend.repositories.PagoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PagoService {
    @Autowired
    PagoRepository pagoRepository;
    static HashMap<String, Integer> PAGO_POR_KLS_LECHE = new HashMap<>();
    static Double RETENCION = 0.13;
    static Integer PAGA_RETENCION = 950000;

    static {
        PAGO_POR_KLS_LECHE.put("A", 700);
        PAGO_POR_KLS_LECHE.put("B", 550);
        PAGO_POR_KLS_LECHE.put("C", 400);
        PAGO_POR_KLS_LECHE.put("D", 250);
    }

    public List<PagoEntity> obtenerPagos() {
        return pagoRepository.findAllByOrderByQuincenaDescProveedorCodigoAsc();
    }

    public List<PagoEntity> obtenerPagosPorQuincena(QuincenaEntity quincena) {
        return pagoRepository.findAllByQuincena(quincena);
    }

    public boolean existenPagosPorQuincena(QuincenaEntity quincena) {
        return pagoRepository.existsByQuincena(quincena);
    }

    public void guardarPago(PagoEntity pago) {
        pago.setId(pago.getProveedor().getCodigo() + "-" + pago.getQuincena().toString());
        pagoRepository.save(pago);
    }

    public void guardarPagos(List<PagoEntity> pagos) {
        for (PagoEntity pago : pagos) {
            guardarPago(pago);
        }
    }

    public PagoEntity calcularPago(DatosCentroAcopioEntity datosCentroAcopio) {
        PagoEntity pago = new PagoEntity();
        pago.setProveedor(datosCentroAcopio.getProveedor());
        pago.setQuincena(datosCentroAcopio.getQuincena());
        pago.setDatosCentroAcopio(datosCentroAcopio);
        pago.setPagoLeche(calcularPagoLeche(datosCentroAcopio));
        pago.setPagoGrasa(calcularPagoGrasa(datosCentroAcopio));
        pago.setPagoSolidoTotal(calcularPagoSolidoTotal(datosCentroAcopio));
        pago.setBonificacionFrecuencia(calcularBonificacionPorFrecuencia(datosCentroAcopio, pago.getPagoLeche()));
        Integer pagoAcopioLeche = pago.getPagoAcopioLeche();
        pago.setDctoVariacionLeche(calcularDctoVariacionLeche(datosCentroAcopio, pagoAcopioLeche));
        pago.setDctoVariacionGrasa(calcularDctoVariacionGrasa(datosCentroAcopio, pagoAcopioLeche));
        pago.setDctoVariacionSolidoTotal(calcularDctoVariacionSolidoTotal(datosCentroAcopio, pagoAcopioLeche));
        pago.setPagoTotal(pago.getPagoAcopioLeche() - pago.getDescuentos());
        pago.setMontoRetencion(calcularMontoRetencion(pago));
        pago.setMontoFinal(pago.getPagoTotal() - pago.getMontoRetencion());
        return pago;
    }

    public List<PagoEntity> calcularPagos(List<DatosCentroAcopioEntity> listaDatosCa) {
        List<PagoEntity> pagos = new ArrayList<>();
        for (DatosCentroAcopioEntity datosCa : listaDatosCa) {
            PagoEntity pago = calcularPago(datosCa);
            pagos.add(pago);
        }

        return pagos;
    }

    public Integer calcularPagoLeche(DatosCentroAcopioEntity datosCentroAcopio) {
        String categoriaProveedor = datosCentroAcopio.getProveedor().getCategoria();
        Integer totalKlsLeche = datosCentroAcopio.getTotalKlsLeche();
        return PAGO_POR_KLS_LECHE.get(categoriaProveedor) * totalKlsLeche;
    }

    public Integer calcularPagoGrasa(DatosCentroAcopioEntity datosCentroAcopio) {
        Integer porcentajeGrasa = datosCentroAcopio.getLaboratorioLeche().getPorcentajeGrasa();
        Integer totalKlsLeche = datosCentroAcopio.getTotalKlsLeche();
        Integer pago;
        if (porcentajeGrasa >= 0 && porcentajeGrasa <= 20) {
            pago = 30 * totalKlsLeche;
        } else if (porcentajeGrasa >= 21 && porcentajeGrasa <= 45) {
            pago = 80 * totalKlsLeche;
        } else {
            pago = 120 * totalKlsLeche;
        }

        return pago;
    }

    public Integer calcularPagoSolidoTotal(DatosCentroAcopioEntity datosCentroAcopio) {
        Integer porcentajeSolidoTotal = datosCentroAcopio.getLaboratorioLeche().getPorcentajeSolidoTotal();
        Integer totalKlsLeche = datosCentroAcopio.getTotalKlsLeche();
        Integer pago;
        if (porcentajeSolidoTotal >= 0 && porcentajeSolidoTotal <= 7) {
            pago = -130 * totalKlsLeche;
        } else if (porcentajeSolidoTotal >= 8 && porcentajeSolidoTotal <= 18) {
            pago = -90 * totalKlsLeche;
        } else if (porcentajeSolidoTotal >= 19 && porcentajeSolidoTotal <= 35) {
            pago = 95 * totalKlsLeche;
        } else {
            pago = 150 * totalKlsLeche;
        }

        return pago;
    }

    public Integer calcularBonificacionPorFrecuencia(DatosCentroAcopioEntity datosCentroAcopio, Integer pagoLeche) {
        Integer pago = 0;
        if (datosCentroAcopio.getDiasEnvioMyT() > 10) {
            pago = (int) Math.floor(0.20 * pagoLeche);
        } else if (datosCentroAcopio.getDiasEnvioM() > 10) {
            pago = (int) Math.floor(0.12 * pagoLeche);
        } else if (datosCentroAcopio.getDiasEnvioT() > 10) {
            pago = (int) Math.floor(0.08 * pagoLeche);
        }

        return pago;
    }

    public Integer calcularDctoVariacionLeche(DatosCentroAcopioEntity datosCentroAcopio, Integer pagoAcopioLeche) {
        Integer variacionNegativaLeche = datosCentroAcopio.getVariacionLeche() * -1;
        Integer descuento = 0;

        if (variacionNegativaLeche >= 9 && variacionNegativaLeche <= 25) {
            descuento = (int) Math.floor(0.07 * pagoAcopioLeche);
        } else if (variacionNegativaLeche >= 26 && variacionNegativaLeche <= 45) {
            descuento = (int) Math.floor(0.15 * pagoAcopioLeche);
        } else if (variacionNegativaLeche >= 46) {
            descuento = (int) Math.floor(0.30 * pagoAcopioLeche);
        }

        return descuento;
    }

    public Integer calcularDctoVariacionGrasa(DatosCentroAcopioEntity datosCentroAcopio, Integer pagoAcopioLeche) {
        Integer variacionNegativaGrasa = datosCentroAcopio.getVariacionGrasa() * -1;
        Integer descuento = 0;

        if (variacionNegativaGrasa >= 16 && variacionNegativaGrasa <= 25) {
            descuento = (int) Math.floor(0.12 * pagoAcopioLeche);
        } else if (variacionNegativaGrasa >= 26 && variacionNegativaGrasa <= 40) {
            descuento = (int) Math.floor(0.20 * pagoAcopioLeche);
        } else if (variacionNegativaGrasa >= 41) {
            descuento = (int) Math.floor(0.30 * pagoAcopioLeche);
        }

        return descuento;
    }

    public Integer calcularDctoVariacionSolidoTotal(DatosCentroAcopioEntity datosCentroAcopio, Integer pagoAcopioLeche) {
        Integer variacionNegativaSolidoTotal = datosCentroAcopio.getVariacionSolidoTotal() * -1;
        Integer descuento = 0;

        if (variacionNegativaSolidoTotal >= 7 && variacionNegativaSolidoTotal <= 12) {
            descuento = (int) Math.floor(0.18 * pagoAcopioLeche);
        } else if (variacionNegativaSolidoTotal >= 13 && variacionNegativaSolidoTotal <= 35) {
            descuento = (int) Math.floor(0.27 * pagoAcopioLeche);
        } else if (variacionNegativaSolidoTotal >= 36) {
            descuento = (int) Math.floor(0.45 * pagoAcopioLeche);
        }

        return descuento;
    }

    public Integer calcularMontoRetencion(PagoEntity pago) {
        Integer pagoTotal = pago.getPagoTotal();
        Integer montoRetencion = 0;
        ProveedorEntity proveedor = pago.getProveedor();
        if (pagoTotal > PAGA_RETENCION && proveedor.estaAfectoARetencion()) {
            montoRetencion = (int) Math.floor(RETENCION * pagoTotal);
        }

        return montoRetencion;
    }
}
