package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import modelo.ActividadAgenda;
import modelo.Mascota;
import modelo.Recordatorio;
import modelo.RegistroSanitario;

public class GestorSaludAgenda {
    private ArrayList<RegistroSanitario> registros = new ArrayList<>();

    public String registrarSalud(Mascota mascota, String tipo, String producto,
                                 LocalDate fecha, LocalDate proxima) {
        String id = "RS-" + (registros.size() + 1);
        RegistroSanitario registro = new RegistroSanitario(id, mascota, tipo, producto, fecha, proxima);
        if (!registro.validar()) return "Error: revisa el producto y las fechas.";
        registros.add(registro);
        return "Registro guardado. ID: " + id;
    }

    public RegistroSanitario buscarRegistro(Mascota mascota, String id) {
        for (RegistroSanitario registro : registros) {
            if (registro.getMascota() == mascota && registro.getId().equalsIgnoreCase(id)) {
                return registro;
            }
        }
        return null;
    }

    public String listarHistorial(Mascota mascota) {
        ArrayList<RegistroSanitario> historial = new ArrayList<>();
        for (RegistroSanitario registro : registros) {
            if (registro.getMascota() == mascota) {
                int posicion = historial.size();
                while (posicion > 0 && historial.get(posicion - 1).getFechaAplicacion()
                        .isAfter(registro.getFechaAplicacion())) {
                    posicion--;
                }
                historial.add(posicion, registro);
            }
        }

        String texto = "";
        for (RegistroSanitario registro : historial) {
            texto += registro.detalle() + "\n";
        }
        if (texto.isEmpty()) return "No hay registros sanitarios.";
        return texto;
    }

    // La opción 5 muestra la agenda ordenada por fecha.
    // Para no saturar la consola, solo muestra cuatro días de actividades repetitivas.

    public String listarAgenda(Mascota mascota) {

        ArrayList<ActividadAgenda> actividades = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (ActividadAgenda actividad : mascota.getAgenda().listarActividades()) {

            LocalDate fecha = actividad.getFechaHoraProgramada().toLocalDate();

            boolean repetitiva = actividad.getFrecuenciaDias() > 0
                    || !actividad.getId().equals(actividad.getIdSerie());

            if (repetitiva && (fecha.isBefore(hoy.minusDays(1))
                    || fecha.isAfter(hoy.plusDays(2)))) {
                continue;
            }

            int posicion = 0;

            while (posicion < actividades.size()
                    && !actividad.getFechaHoraProgramada()
                    .isBefore(actividades.get(posicion).getFechaHoraProgramada())) {
                posicion++;
            }

            actividades.add(posicion, actividad);
        }

        if (actividades.isEmpty()) {
            return "No hay actividades programadas.";
        }

        String texto = "=== AGENDA ===\n";

        for (ActividadAgenda actividad : actividades) {

            String estado = actividad.getEstado();

            if (actividad.estaVencida(LocalDateTime.now())) {
                estado = "Vencida";
            }

            texto += "\n" + actividad.getNombre()
                    + "\nFecha: " + actividad.getFechaHoraProgramada()
                    + "\nEstado: " + estado
                    + "\nID: " + actividad.getId() + "\n";

            if (actividad.getFechaHoraRealizada() != null) {
                texto += "Realizada: " + actividad.getFechaHoraRealizada();

                if (actividad.getResponsable() != null) {
                    texto += " | Responsable: " + actividad.getResponsable();
                }

                texto += "\n";
            }
        }

        return texto;
    }


    public String listarCitas(Mascota mascota, boolean realizadas) {
        String texto = "";
        LocalDateTime ahora = LocalDateTime.now();
        for (ActividadAgenda actividad : mascota.getAgenda().listarActividades()) {
            if (ActividadAgenda.CONSULTA.equals(actividad.getCategoria())) {
                boolean realizada = ActividadAgenda.REALIZADA.equals(actividad.getEstado());
                boolean futura = ActividadAgenda.PENDIENTE.equals(actividad.getEstado())
                        && !actividad.estaVencida(ahora);
                if ((realizadas && realizada) || (!realizadas && futura)) {
                    texto += actividad.detalle() + "\n";
                }
            }
        }
        if (texto.isEmpty()) return "No hay citas para mostrar.";
        return texto;
    }

    public String listarRecordatorios(Mascota mascota) {
        String texto = "";
        LocalDateTime ahora = LocalDateTime.now();
        for (ActividadAgenda actividad : mascota.getAgenda().listarActividades()) {
            for (Recordatorio aviso : actividad.getRecordatorios()) {
                if (aviso.debeMostrarse(ahora)) texto += aviso.detalle(ahora) + "\n";
            }
        }
        if (texto.isEmpty()) return "No hay recordatorios activos.";
        return texto;
    }
}
