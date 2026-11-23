package modelo;

import java.time.LocalDate;

public class PlanActividad {
    private final String id;
    private final Mascota mascota;
    private final String tipoActividad;
    private final int duracionMinutos;
    private final int frecuenciaSemanal;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;

    public PlanActividad(String id, Mascota mascota, String tipoActividad, int duracionMinutos,
                         int frecuenciaSemanal, LocalDate inicio, LocalDate fin) {
        this.id = id;
        this.mascota = mascota;
        this.tipoActividad = tipoActividad;
        this.duracionMinutos = duracionMinutos;
        this.frecuenciaSemanal = frecuenciaSemanal;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    public String getId() { return id; }
    public Mascota getMascota() { return mascota; }
    public String getTipoActividad() { return tipoActividad; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }

    public boolean estaVigente(LocalDate fecha) {
        return fecha != null && !fecha.isBefore(fechaInicio)
                && (fechaFin == null || fecha.isBefore(fechaFin));
    }

    public boolean cerrar(LocalDate fin) {
        if (fin == null || !fin.isAfter(fechaInicio)
                || (fechaFin != null && !fin.isBefore(fechaFin))) {
            return false;
        }
        fechaFin = fin;
        return true;
    }

    public long calcularMinutosSemanales() {
        return (long) duracionMinutos * frecuenciaSemanal;
    }
}
