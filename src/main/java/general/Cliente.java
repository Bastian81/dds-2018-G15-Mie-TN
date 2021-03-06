package general;
import dispositivo.Dispositivo;
import dispositivo.Inteligente;
import dispositivo.Estandar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import geoposicionamiento.Posicion;
import geoposicionamiento.Transformador;
import geoposicionamiento.ZonaGeografica;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.joda.time.DateTime;
import controlador.Sensor;
import org.joda.time.TimeOfDay;

import javax.persistence.*;

@Entity
@Table (name = "Clientes")
public class Cliente extends Usuario
{
/*
Agregar @NotFound (action=NotFoundAction.IGNORE) si puede no haber relacion en MNY->SMTHING ->define modalidad
Tambien ver de agregar en ej @OneToMany (cascade=CascadeType.PERSIST) -> refresca lista si se actualizo
sin necesidad de volver a persistir la entidad cliente en este caso */

	int puntos;
    String tipoDocumento;
    int documento;
    int telefonoContacto;
    String domicilio;

/*
	@ManyToOne
			@JoinColumn(name = "Categoria_ID")
			*/
	@ManyToOne( cascade={CascadeType.REFRESH,CascadeType.MERGE,CascadeType.PERSIST})
		@JoinColumn( name = "categoria_id")
	Categoria categoria;


	/*		@JoinTable (
                    name = "Dispositivos por Cliente",
                    joinColumns = @JoinColumn(name = "Cliente ID"),
                    inverseJoinColumns = @JoinColumn(name = "Inteligente ID")
            )

    @OneToMany(
    		mappedBy = "Cliente",
			cascade = CascadeType.ALL,
			orphanRemoval = true

	)
	@JoinColumn (name = "Dipositivo ID")
	*/
	/*
	@OneToMany
			@JoinColumn(name = "dispositivos")
	*/
	@OneToMany(cascade=CascadeType.ALL)
    List<Inteligente> dispositivosInteligentes = new ArrayList<>();

    //@OneToMany
	//		@JoinColumn (name = "Estandares ID")
	@OneToMany(cascade=CascadeType.ALL)
	List<Estandar> dipositivosEstandares = new ArrayList<>();

    DateTime fechaDeAlta;

   // @OneToMany
	//		@JoinColumn (name = "Sensores ID")
   @OneToMany(cascade=CascadeType.ALL)
   List<Sensor> sensores = new ArrayList<>();

    //@OneToOne
	//@JoinColumn (name = "Posicion ID")
	@Embedded
	Posicion posicion;


	private transient SimplexManager simplexManager;



	//@ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne
	public Transformador transformador;

	public List<Inteligente> getDispositivosInteligentes() {
		return dispositivosInteligentes;
	}

	//Constructor//
public Cliente(String nom, String ap, String nomUsuario, String contra, String direccion,String tipoDoc,int doc, int tel, double unaLatitud, double unaLongitud, ArrayList<Sensor> unosSensores)
{
	super(nom,ap,nomUsuario,contra);
	tipoDocumento = tipoDoc;
	documento = doc;
	telefonoContacto = tel;
	domicilio = direccion;
	DateTime alta = new DateTime();
	this.fechaDeAlta = alta;
	puntos = 0;
	double latitud = unaLatitud;
	double longitud = unaLongitud;
	posicion = new Posicion(latitud, longitud);
	simplexManager = new SimplexManager();
	sensores = unosSensores;
}

	public Cliente(){}

	public Cliente(String nom, String ap, String nomUsuario, String contra, int puntos, String tipoDocumento, int documento, int telefonoContacto, String domicilio, Categoria categoria, List<Inteligente> dispositivosInteligentes, List<Estandar> dipositivosEstandares, DateTime fechaDeAlta, List<Sensor> sensores, Posicion posicion, SimplexManager simplexManager)
	{
		super(nom, ap, nomUsuario, contra);
		this.puntos = puntos;
		this.tipoDocumento = tipoDocumento;
		this.documento = documento;
		this.telefonoContacto = telefonoContacto;
		this.domicilio = domicilio;
		this.categoria = categoria;
		this.dispositivosInteligentes = dispositivosInteligentes;
		this.dipositivosEstandares = dipositivosEstandares;
		this.fechaDeAlta = fechaDeAlta;
		this.sensores = sensores;
		this.posicion = posicion;
		this.simplexManager = simplexManager;
	}

	public Cliente(String nom, String ap, String nomUsuario, String contra, String tipoDocumento, int documento, int telefonoContacto, String domicilio, float latitud,float longitud)
	{
		super(nom, ap, nomUsuario, contra);
		this.puntos = 0;
		this.tipoDocumento = tipoDocumento;
		this.documento = documento;
		this.telefonoContacto = telefonoContacto;
		this.domicilio = domicilio;
		this.fechaDeAlta = new DateTime();
		this.posicion = new Posicion(latitud,longitud);
	}

	public Cliente(String nom, String ap, String nomUsuario, String contra, String tipoDocumento, int documento, int telefonoContacto, String domicilio, float latitud,float longitud,List<Inteligente> dispositivosInteligentes)
	{
		super(nom, ap, nomUsuario, contra);
		this.puntos = 0;
		this.tipoDocumento = tipoDocumento;
		this.documento = documento;
		this.telefonoContacto = telefonoContacto;
		this.domicilio = domicilio;
		this.fechaDeAlta = new DateTime();
		this.posicion = new Posicion(latitud,longitud);
		this.dispositivosInteligentes = dispositivosInteligentes;
	}

	public Cliente(String nom, String ap, String nomUsuario, String contra, String tipoDocumento, int documento, int telefonoContacto, String domicilio, float latitud,float longitud,List<Inteligente> dispositivosInteligentes, List<Sensor> sensores)
	{
		super(nom, ap, nomUsuario, contra);
		this.puntos = 0;
		this.tipoDocumento = tipoDocumento;
		this.documento = documento;
		this.telefonoContacto = telefonoContacto;
		this.domicilio = domicilio;
		this.fechaDeAlta = new DateTime();
		this.posicion = new Posicion(latitud,longitud);
		this.dispositivosInteligentes = dispositivosInteligentes;
		this.sensores = sensores;
	}


//SIMPLEX//

		public double getHorasMaxRecomendadas(Inteligente disp, List<Inteligente> disps)
	{
		simplexManager.procesardispositivosInteligentes(disps);
		return simplexManager.getHorasRecomendadas(disps, disp);
	}

//SIMPLEX//


//CONTROLADORES//

	public void agregarSensor (Sensor unSensor) {
    sensores.add(unSensor);
}

//CONTROLADORES//


//dispositivosInteligentes//

	public int cantdispositivosInteligentesTotal()
	{
		return dispositivosInteligentes.size() + dipositivosEstandares.size();
	}

	public int cantdispositivosInteligentesEncendidos()
	{
		return dispositivosInteligentesEncendidos().size();
	}

	public int cantdispositivosInteligentesApagados()
	{
		return cantdispositivosInteligentesTotal() - cantdispositivosInteligentesEncendidos();
	}

		//Adaptar estos metodos

	public List<Dispositivo> dispositivosInteligentesEncendidos()
	{
		return dispositivosInteligentes.stream().filter(unDispositivo -> unDispositivo.estaEncendido())
				 .collect(Collectors.toList());
	}

	public boolean algunoEncendido()
	{
		return dispositivosInteligentes.stream().anyMatch(unDispositivo->unDispositivo.estaEncendido());
	}

	public void agregarInteligente(Inteligente nuevoDispositivo)
	{

		dispositivosInteligentes.add(nuevoDispositivo);
		if(!serialRepetida(nuevoDispositivo.nroSerial())) {
			puntos += nuevoDispositivo.puntos();
		}
	}

	public void agregarInteligentes(List<Inteligente> nuevosDispositivos)
	{

		for(Inteligente intel: nuevosDispositivos)
		{
			agregarInteligente(intel);
		}

	}

	public void agregarEstandar(Estandar nuevoDispositivo)
	{

		dipositivosEstandares.add(nuevoDispositivo);
	}

	public float consumoMensual()
	{
		float consumo = 0;

		for(Dispositivo dispositivo: dispositivosInteligentes)
			consumo+= dispositivo.getConsumo();

		return consumo;

	}

	public boolean serialRepetida(int serial_nueva)
	{
		return dispositivosInteligentes.stream().anyMatch(unDispositivo->(unDispositivo.nroSerial() == serial_nueva)); //No me importa si es estandar ya que no da puntos
	}

//dispositivosInteligentes//

//CATEGORIAS//

	public Categoria getCategoria()
	{
		return this.categoria;
	}

	public float estimarFacturacion(List<Categoria> categorias){
			return 	categoria.estimarFacturacionMensual(this);
		}


	public void setCategoria(List<Categoria> categorias)
	{

		categorias.sort(Comparator.comparingDouble(Categoria::consumoMensualMin));

		this.categoria = (categorias.stream()
				.filter(c->c.pertence(this)).findFirst()
				.get());
	}

	public String nombreCategoria()
	{
			return categoria.tipo();
	}

//CATEGORIAS//

//GEOPOSICIONAMIENTO//

	public Posicion getPosicion() {
		return posicion;
	}

	// Metodo para asignar un transformador a un cliente.
	public void asignarTransformador(List<ZonaGeografica> listaZonas)
	{
		Transformador transformadorDesignado = null;
		Transformador aux;
		int i=0;

		for(ZonaGeografica zonaG : listaZonas)
		{
			if(zonaG.clientePertenece(this))
			{
				ArrayList<Transformador> listaTransf = new ArrayList<Transformador> ();

				aux = zonaG.asignarTransformador(this);

				listaTransf.add(aux);

				if(i == 0){
					transformadorDesignado = aux;
					i++;
				}
				else{

					if(zonaG.existeOtroMasCercano(this.posicion, transformadorDesignado.getPosicion(), listaTransf)) {
						transformadorDesignado = aux;
						i++;
					}
				}
			}
		}

		transformadorDesignado.agregarCliente(this);
	}

	public float consumoPeriodo(DateTime fechaInicio,DateTime fechaFin)
	{
		float consumo = 0;

		for(Inteligente dispositivoInteligente: dispositivosInteligentes)
			consumo+= dispositivoInteligente.consumoPeriodo(fechaInicio,fechaFin);

		return consumo;

	}

//GEOPOSICIONAMIENTO//

}

