package modelo;

import java.time.LocalDateTime;

public class Recordatorio {
    private ActividadAgenda actividad;
    private String mensaje;
    private int diasAnticipacion;

    public Recordatorio(ActividadAgenda actividad, String mensaje, int dias) {
        this.actividad = actividad;
        this.mensaje = mensaje;
        diasAnticipacion = dias;
    }

    public int getDiasAnticipacion() { return diasAnticipacion; }

    public LocalDateTime calcularFechaAviso() {
        return actividad.getFechaHoraProgramada().minusDays(diasAnticipacion);
    }

    public boolean esDuplicado(Recordatorio otro) {
        return otro != null && actividad == otro.actividad && diasAnticipacion == otro.diasAnticipacion;
    }

    public boolean debeMostrarse(LocalDateTime ahora) {
        return ActividadAgenda.PENDIENTE.equals(actividad.getEstado())
                && !ahora.isBefore(calcularFechaAviso());
    }

    public String detalle(LocalDateTime ahora) {
        String estado = "Próximo";
        if (actividad.estaVencida(ahora)) estado = "Vencido";
        return estado + " | " + mensaje + " | Aviso " + calcularFechaAviso()
                + " | Evento " + actividad.getFechaHoraProgramada();
    }
}
