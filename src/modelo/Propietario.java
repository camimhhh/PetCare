package modelo;

import java.util.ArrayList;

// RF1: representa al dueno de una o varias mascotas.
public class Propietario {
    private String id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private ArrayList<Mascota> mascotas;

    public Propietario(String id, String nombres, String apellidos,
                       String correo, String telefono) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.mascotas = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getNombreCompleto() { return nombres + " " + apellidos; }
    public ArrayList<Mascota> getMascotas() { return mascotas; }

    // Este metodo se ejecuta DESPUES de validar en GestorPerfiles.
    public void modificarDatos(String nombres, String apellidos,
                               String correo, String telefono) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
    }

    public boolean registrarMascota(Mascota mascota) {
        if (mascota == null || mascota.getPropietario() != this) {
            return false;
        }
        if (buscarMascota(mascota.getId()) != null) {
            return false;
        }
        mascotas.add(mascota);
        return true;
    }

    public Mascota buscarMascota(String idMascota) {
        if (idMascota == null) {
            return null;
        }
        for (Mascota mascota : mascotas) {
            if (mascota.getId().equalsIgnoreCase(idMascota.trim())) {
                return mascota;
            }
        }
        return null;
    }
}
