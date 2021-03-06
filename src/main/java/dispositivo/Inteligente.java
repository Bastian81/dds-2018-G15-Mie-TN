package dispositivo;
import estado.*;
import general.Cliente;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
@Table (name = "Inteligentes")
public class Inteligente extends Dispositivo {


    private String macAddress;
    @ManyToOne
    private Estado estado;
    @OneToMany(cascade=CascadeType.ALL)
    public List<HistorialConsumo> historialConsumo;

    int idCliente;


    public Inteligente(String nombre, Float consumo, int nroSerial, String macAddress, Estado estado)
    {
        super(nombre,consumo,nroSerial);
        this.macAddress = macAddress;
        this.estado = estado;
        this.historialConsumo = new ArrayList<>();
    }

    public Inteligente(String nombre, Float consumo, int nroSerial, String nombreUsuario, String macAddress, Estado estado) {
        super(nombre, consumo, nroSerial, nombreUsuario);
        this.macAddress = macAddress;
        this.estado = estado;
        this.historialConsumo = new ArrayList<>();
    }

    public HistorialConsumo ultimoLista() {
    	if(historialConsumo.isEmpty())
    		return null;
    	else
    		return historialConsumo.get(historialConsumo.size() - 1);
    }


    public void cambiarEstado(Estado nuevoEstado)
    {
        this.estado = nuevoEstado;
        if( historialConsumo.isEmpty() || estado != ultimoLista().getEstadoActual())
        {
            HistorialConsumo nuevoCambio = new HistorialConsumo(estado,new DateTime());
            historialConsumo.add(nuevoCambio);
        }
    }

    // Metodos del Estado

    public void apagar()
    {
        estado.apagar(this);
    }
    public void encender()
    {
        estado.encender(this);
    }
    public void modoAhorro()
    {
        estado.modoAhorro(this);
    }

    public Boolean estaEncendido() {return estado.estaEncendido();}

    
    // Metodos para calcular el consumo

    public double getConsumoPorHoras(int cantidadHoras)             //Cuanto consumio en las ultimas horas
    {
        return consumoPeriodo(obtenerFechaAnterior(cantidadHoras),new DateTime());
    }
    public DateTime obtenerFechaAnterior(int cantHoras)
    {
        return new DateTime().plusHours(-cantHoras);
    }


    public double consumoPeriodo(DateTime fechaInicio,DateTime fechaFin)       //Consumo comprendido entre dos periodos
    {
        List<HistorialConsumo> listaAux;
        listaAux = historialConsumo.stream().filter(unCambioHistorial -> betweenPeriodo(unCambioHistorial.getFechaEstado(),fechaInicio,fechaFin)).collect(Collectors.toList());
       return listaAux.stream().mapToDouble(unCambioHistorial -> calculoConsumo(unCambioHistorial,listaAux)).sum();
    }

    public double calculoConsumo(HistorialConsumo cambioHistorial, List<HistorialConsumo> listaCambiosHistorial)
    {
        return cambioHistorial.getEstadoActual().porcentajeAhorroConsumo()*this.getConsumo()*cambioHistorial.tiempoUso(listaCambiosHistorial);
    }
    
    public double getUsoTotalDelMesActualEnHoras()
    {
    	return historialConsumo.get(0).tiempoUso(historialConsumo);
    }


    public Boolean betweenPeriodo (DateTime fechaEstado,DateTime fechaInicio,DateTime fechaFin)
    {
        return fechaInicio.isBefore(fechaEstado) && fechaEstado.isBefore(fechaFin);
    }

    public Boolean esInt() {return true;}
    public int puntos() {return 15;}

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public List<HistorialConsumo> getHistorialConsumo() {
        return historialConsumo;
    }

    public void setHistorialConsumo(List<HistorialConsumo> historialConsumo) {
        this.historialConsumo = historialConsumo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }


}
