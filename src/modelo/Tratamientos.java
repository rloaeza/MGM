package modelo;

public class Tratamientos {
    private int idTratamiento;
    private String nombre;
    private String descripcion;


    private int sesiones;
    private int idClinica;

    public Tratamientos() {
    }

    public Tratamientos(int idTratamiento, String nombre, String descripcion, int sesiones, int idClinica) {
        this.idTratamiento = idTratamiento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.sesiones = sesiones;
        this.idClinica = idClinica;
    }

    public int getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(int idTratamiento) {
        this.idTratamiento = idTratamiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(int idClinica) {
        this.idClinica = idClinica;
    }

    public int getSesiones() {
        return sesiones;
    }

    public void setSesiones(int sesiones) {
        this.sesiones = sesiones;
    }


    @Override
    public String toString() {
        return getNombre();
    }
}
