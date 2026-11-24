package modelo;

import java.time.LocalDate;

public class RegistroSanitario {
    private String id;
    private Mascota mascota;
    private String tipo;
    private String nombreProducto;
    private LocalDate fechaAplicacion;
    private LocalDate proximaFecha;

    public RegistroSanitario(String id, Mascota mascota, String tipo, String producto,
                             LocalDate fecha, LocalDate proxima) {
        this.id = id;
        this.mascota = mascota;
        this.tipo = tipo;
        nombreProducto = producto;
        fechaAplicacion = fecha;
        proximaFecha = proxima;
    }

    public String getId() { return id; }
    public Mascota getMascota() {
        return mascota;
    }
    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public boolean validar() {
        if (!"Vacuna".equals(tipo) && !"Desparasitación".equals(tipo)) return false;
        if (mascota == null || nombreProducto == null || nombreProducto.trim().isEmpty()) return false;
        if (fechaAplicacion == null || fechaAplicacion.isAfter(LocalDate.now())) return false;
        return !tieneProximaFecha() || proximaFecha.isAfter(fechaAplicacion);
    }

    public boolean tieneProximaFecha() {
        return proximaFecha != null;
    }

    public boolean modificarRegistro(String tipo, String producto, LocalDate fecha, LocalDate proxima) {
        RegistroSanitario cambios = new RegistroSanitario(id, mascota, tipo, producto, fecha, proxima);
        if (!cambios.validar()) return false;
        this.tipo = tipo;
        nombreProducto = producto;
        fechaAplicacion = fecha;
        proximaFecha = proxima;
        return true;
    }

    public String detalle() {
        String texto = id + " | " + fechaAplicacion + " | " + tipo + " | " + nombreProducto;
        if (tieneProximaFecha()) texto += " | Próxima fecha " + proximaFecha;
        return texto;
    }
}
