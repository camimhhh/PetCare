package modelo;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

// RF2: representa el perfil de un perro o gato.
public class Mascota {
    private String id;
    private String nombre;
    private String especie;
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private double peso;
    private Propietario propietario;
    private Agenda agenda;
    // Los productos pertenecen a esta mascota. El modulo 4 reutilizara esta lista.
    private ArrayList<Producto> productos;

    public Mascota(String id, String nombre, String especie, String raza,
                   String sexo, LocalDate fechaNacimiento, double peso,
                   Propietario propietario) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.propietario = propietario;
        agenda = new Agenda(this);
        productos = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public String getSexo() { return sexo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public double getPeso() { return peso; }
    public Propietario getPropietario() { return propietario; }

    public Agenda getAgenda() { return agenda; }

    public ArrayList<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public Producto buscarProducto(String nombre) {
        if (nombre == null) return null;
        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre.trim())) return producto;
        }
        return null;
    }

    public boolean agregarProducto(Producto producto) {
        if (producto == null || producto.getMascota() != this
                || buscarProducto(producto.getNombre()) != null) return false;
        productos.add(producto);
        return true;
    }

    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    // Los datos son validados antes en GestorPerfiles.
    public void modificarDatos(String nombre, String especie, String raza,
                               String sexo, LocalDate fechaNacimiento, double peso) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
    }
}
