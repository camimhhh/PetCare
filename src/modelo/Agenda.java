package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Agenda {
    private String id;
    private Mascota mascota;
    private ArrayList<ActividadAgenda> actividades = new ArrayList<>();

    public Agenda(Mascota mascota) {
        this.mascota = mascota;
        id = "AG-" + mascota.getId();
    }

    public Mascota getMascota() { return mascota; }

    String siguienteId() {
        return id + "-" + (actividades.size() + 1);
    }

    public boolean programarActividad(String nombre, String categoria, LocalDate fecha,
                                      LocalTime hora) {
        return programarActividad(nombre, categoria, fecha, hora, 0);
    }

    public boolean programarActividad(String nombre, String categoria, LocalDate fecha,
                                      LocalTime hora, int frecuenciaDias) {
        if (nombre == null || nombre.trim().isEmpty() || fecha == null || hora == null
                || frecuenciaDias < 0 || !ActividadAgenda.categoriaValida(categoria)) {
            return false;
        }
        actualizarRecurrentes();
        ActividadAgenda nueva = new ActividadAgenda(siguienteId(), this, nombre.trim(),
                categoria, fecha, hora, frecuenciaDias);
        return agregarActividad(nueva);
    }

    boolean agregarActividad(ActividadAgenda nueva) {
        for (ActividadAgenda actividad : actividades) {
            if (actividad.esMismaProgramacion(nueva)) return false;
        }
        actividades.add(nueva);
        return true;
    }

    void agregarAvisoASerie(String idSerie, int dias) {
        for (ActividadAgenda actividad : actividades) {
            if (actividad.getIdSerie().equals(idSerie)) {
                actividad.agregarRecordatorioLocal(dias);
            }
        }
    }

    // Se generan eventos individuales aunque los anteriores NO estén realizados.
    // Para no llenar la memoria, se muestran los últimos 30 días y los próximos 7.
    public void actualizarRecurrentes() {
        LocalDate desde = LocalDate.now().minusDays(30);
        LocalDate hasta = LocalDate.now().plusDays(7);
        ArrayList<ActividadAgenda> originales = new ArrayList<>(actividades);
        for (ActividadAgenda original : originales) {
            int frecuencia = original.getFrecuenciaDias();
            if (frecuencia <= 0) continue;

            LocalDate inicio = original.getFechaHoraProgramada().toLocalDate();
            // Avanzamos la fecha hasta el periodo que queremos consultar.
            // "frecuencia" siempre es mayor que cero, por eso el ciclo avanza.
            LocalDate fecha = inicio;
            while (fecha.isBefore(desde)) {
                fecha = fecha.plusDays(frecuencia);
            }

            while (!fecha.isAfter(hasta)) {
                if (fecha.isAfter(inicio)) {
                    agregarActividad(original.crearOcurrencia(fecha));
                }
                fecha = fecha.plusDays(frecuencia);
            }
        }
    }

    public ActividadAgenda buscarActividad(String id) {
        actualizarRecurrentes();
        if (id == null) return null;
        for (ActividadAgenda actividad : actividades) {
            if (actividad.getId().equalsIgnoreCase(id.trim())) return actividad;
        }
        return null;
    }

    public ArrayList<ActividadAgenda> listarActividades() {
        actualizarRecurrentes();
        return new ArrayList<>(actividades);
    }
}
