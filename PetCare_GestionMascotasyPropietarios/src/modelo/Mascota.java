package modelo;

import java.time.LocalDate;
import java.time.Period;

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
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public String getSexo() { return sexo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public double getPeso() { return peso; }
    public Propietario getPropietario() { return propietario; }

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
