package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class ActividadAgenda {
    public static final String CONSULTA = "Consulta veterinaria";
    public static final String COMIDA = "Comida";
    public static final String PENDIENTE = "Pendiente";
    public static final String REALIZADA = "Realizada";

    private String id;
    private Agenda agenda;
    private String actividad;
    private String categoria;
    private LocalDate fechaProgramada;
    private LocalTime horaProgramada;
    private String estado = PENDIENTE;
    private LocalDateTime fechaHoraRealizada;
    private String responsable;
    private boolean recurrente;
    private int frecuenciaDias;
    private String idSerie; // mismo valor para cada ocurrencia de una actividad recurrente
    private ArrayList<Recordatorio> recordatorios = new ArrayList<>();

    ActividadAgenda(String id, Agenda agenda, String actividad, String categoria,
                    LocalDate fecha, LocalTime hora, int frecuenciaDias) {
        this.id = id;
        this.agenda = agenda;
        this.actividad = actividad;
        this.categoria = categoria;
        fechaProgramada = fecha;
        horaProgramada = hora;
        this.frecuenciaDias = frecuenciaDias;
        recurrente = frecuenciaDias > 0;
        idSerie = id;
    }

    public String getId() { return id; }
    public String getNombre() { return actividad; }
    public LocalDateTime getFechaHoraRealizada() { return fechaHoraRealizada; }
    public String getResponsable() { return responsable; }
    public String getEstado() { return estado; }
    public String getCategoria() { return categoria; }
    public Agenda getAgenda() { return agenda; }
    public int getFrecuenciaDias() { return frecuenciaDias; }
    public String getIdSerie() { return idSerie; }

    public static boolean categoriaValida(String categoria) {
        return CONSULTA.equals(categoria) || "Paseo".equals(categoria)
                || "Baño".equals(categoria) || "Vacuna".equals(categoria)
                || COMIDA.equals(categoria);
    }

    public LocalDateTime getFechaHoraProgramada() {
        return LocalDateTime.of(fechaProgramada, horaProgramada);
    }

    public boolean estaVencida(LocalDateTime ahora) {
        return PENDIENTE.equals(estado) && getFechaHoraProgramada().isBefore(ahora);
    }

    public boolean marcarRealizada(LocalDateTime fecha, String quien) {
        if (COMIDA.equals(categoria)) return false;
        return guardarCumplimiento(fecha, quien);
    }

    // Inventario llama este método al registrar el consumo del evento.
    public boolean marcarComidaConConsumo(LocalDateTime fecha, String quien) {
        if (!COMIDA.equals(categoria)) return false;
        return guardarCumplimiento(fecha, quien);
    }

    private boolean guardarCumplimiento(LocalDateTime fecha, String quien) {
        if (!PENDIENTE.equals(estado) || fecha == null || fecha.isAfter(LocalDateTime.now())) {
            return false;
        }
        // El calendario genera las ocurrencias, aunque la anterior siga pendiente.
        estado = REALIZADA;
        fechaHoraRealizada = fecha;
        responsable = "";
        if (quien != null) {
            responsable = quien.trim();
        }
        return true;
    }

    public boolean cancelar() {
        if (!PENDIENTE.equals(estado)) return false;
        estado = "Cancelada";
        return true;
    }

    public boolean deshacerRegistro() {
        if (!REALIZADA.equals(estado) || COMIDA.equals(categoria)) return false;
        estado = PENDIENTE;
        fechaHoraRealizada = null;
        responsable = null;
        return true;
    }

    // Crea una fecha individual, sin modificar el estado de las anteriores.
    public ActividadAgenda generarSiguienteOcurrencia() {
        if (!recurrente) return null;
        return crearOcurrencia(fechaProgramada.plusDays(frecuenciaDias));
    }

    ActividadAgenda crearOcurrencia(LocalDate fecha) {
        // Una ocurrencia individual no vuelve a generar toda la serie.
        ActividadAgenda nueva = new ActividadAgenda(agenda.siguienteId(), agenda,
                actividad, categoria, fecha, horaProgramada, 0);
        nueva.idSerie = idSerie;
        for (Recordatorio aviso : recordatorios) {
            nueva.agregarRecordatorioLocal(aviso.getDiasAnticipacion());
        }
        return nueva;
    }

    public boolean esMismaProgramacion(ActividadAgenda otra) {
        return otra != null && agenda == otra.agenda && actividad.equalsIgnoreCase(otra.actividad)
                && categoria.equals(otra.categoria)
                && getFechaHoraProgramada().equals(otra.getFechaHoraProgramada());
    }

    public boolean agregarRecordatorio(int dias) {
        if (!agregarRecordatorioLocal(dias)) return false;
        // Si se configura en la actividad original, se aplica a su serie.
        if (recurrente) agenda.agregarAvisoASerie(idSerie, dias);
        return true;
    }

    boolean agregarRecordatorioLocal(int dias) {
        if (dias < 0 || !PENDIENTE.equals(estado)) return false;
        Recordatorio nuevo = new Recordatorio(this, actividad, dias);
        for (Recordatorio aviso : recordatorios) {
            if (aviso.esDuplicado(nuevo)) return false;
        }
        recordatorios.add(nuevo);
        return true;
    }

    public ArrayList<Recordatorio> getRecordatorios() {
        return new ArrayList<>(recordatorios);
    }

    public String detalle() {
        String situacion = estado;
        if (estaVencida(LocalDateTime.now())) situacion = "Vencida";
        String texto = id + " | " + fechaProgramada + " " + horaProgramada
                + " | " + actividad + " (" + categoria + ") | " + situacion;
        if (fechaHoraRealizada != null) texto += " | Cumplida " + fechaHoraRealizada;
        if (responsable != null && !responsable.isEmpty()) texto += " | " + responsable;
        return texto;
    }
}
