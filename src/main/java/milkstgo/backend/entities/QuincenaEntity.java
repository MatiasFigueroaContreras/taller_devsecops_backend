package milkstgo.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
@Table(name = "quincena")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuincenaEntity {
    @Id
    private String id;
    private Integer year;
    private Integer mes;
    private Integer numero;

    public QuincenaEntity(Integer year, Integer mes, Integer numero){
        this.year = year;
        this.mes = mes;
        this.numero = numero;
        this.id = toString();
    }

    public String toString(){
        String mesFormateado = mes.toString();
        if(mesFormateado.length() == 1){
            mesFormateado = "0" + mesFormateado;
        }
        return year.toString() + "/" + mesFormateado + "/" + numero.toString();
    }

    public boolean estaDentroQuincena(Date fecha){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(fecha);
        int mesFecha = calendar.get(Calendar.MONTH) + 1;
        int yearFecha = calendar.get(Calendar.YEAR);
        int diaFecha = calendar.get(Calendar.DAY_OF_MONTH);
        if(numero == 1 && diaFecha > 15){
            return false;
        } else if (numero == 2 && diaFecha < 15) {
            return false;
        }
        return yearFecha == year && mesFecha == mes;
    }

    public QuincenaEntity obtenerQuincenaAnterior(){
        QuincenaEntity quincenaAnterior = new QuincenaEntity();
        if(numero == 2){
            quincenaAnterior.setNumero(1);
            quincenaAnterior.setMes(mes);
            quincenaAnterior.setYear(year);
        }
        else if(mes == 1){
            quincenaAnterior.setNumero(2);
            quincenaAnterior.setMes(12);
            quincenaAnterior.setYear(year - 1);
        }
        else {
            quincenaAnterior.setNumero(2);
            quincenaAnterior.setMes(mes - 1);
            quincenaAnterior.setYear(year);
        }
        quincenaAnterior.setId(quincenaAnterior.toString());
        return quincenaAnterior;
    }
}
