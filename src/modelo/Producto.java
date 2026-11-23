package modelo;

// Producto alimenticio compartido entre los planes de una misma mascota.
// En el modulo 4 se completara su manejo de compras y consumos.
public class Producto {
    private final String id;
    private final Mascota mascota;
    private final String nombre;
    private double gramosDisponibles;

    public Producto(String id, Mascota mascota, String nombre, double gramosDisponibles) {
        this.id = id;
        this.mascota = mascota;
        this.nombre = nombre;
        this.gramosDisponibles = gramosDisponibles;
    }

    public String getId() { return id; }
    public Mascota getMascota() { return mascota; }
    public String getNombre() { return nombre; }
    public double getGramosDisponibles() { return gramosDisponibles; }
}
