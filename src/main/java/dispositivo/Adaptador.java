package dispositivo;

import estado.Estado;

public class Adaptador extends Inteligente {

    public Adaptador(String nombre, Float consumo, int nroSerial, byte macAddress, Estado estado)
    {
        super(nombre,consumo,nroSerial,macAddress,estado);
    }

    public int puntos() {return 10;}
}
