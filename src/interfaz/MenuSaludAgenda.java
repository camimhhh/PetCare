package interfaz;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import modelo.ActividadAgenda;
import modelo.Mascota;
import modelo.RegistroSanitario;
import negocio.GestorPerfiles;
import negocio.GestorSaludAgenda;

// Módulo 3: RF5 (salud) y RF6 (agenda y recordatorios).
public class MenuSaludAgenda {
    private EntradaConsola entrada;
    private GestorPerfiles perfiles;
    private GestorSaludAgenda salud;

    public MenuSaludAgenda(EntradaConsola entrada, GestorPerfiles perfiles,
                           GestorSaludAgenda salud) {
        this.entrada = entrada;
        this.perfiles = perfiles;
        this.salud = salud;
    }

    public void abrir() {
        String id = entrada.idMascota();
        if (id == null) return;
        Mascota mascota = perfiles.buscarMascota(id);
        String opcion = "";
        while (!opcion.equals("0")) {
            System.out.println("\n----  " + mascota.getNombre() + " ----");
            System.out.println("1. Registrar vacuna o desparasitación");
            System.out.println("2. Ver historial sanitario");
            System.out.println("3. Modificar registro sanitario");
            System.out.println("4. Programar actividad");
            System.out.println("5. Ver agenda");
            System.out.println("6. Ver citas veterinarias futuras");
            System.out.println("7. Ver citas veterinarias realizadas");
            System.out.println("8. Agregar recordatorio");
            System.out.println("9. Ver recordatorios activos");
            System.out.println("10. Marcar actividad realizada");
            System.out.println("11. Cancelar actividad");
            System.out.println("12. Deshacer cumplimiento");
            System.out.println("0. Volver");
            opcion = entrada.pedir("Opción: ").trim();
            switch (opcion) {
                case "1": registrarSalud(mascota, null); break;
                case "2": System.out.println(salud.listarHistorial(mascota)); break;
                case "3": modificarSalud(mascota); break;
                case "4": programar(mascota); break;
                case "5": System.out.println(salud.listarAgenda(mascota)); break;
                case "6": System.out.println(salud.listarCitas(mascota, false)); break;
                case "7": System.out.println(salud.listarCitas(mascota, true)); break;
                case "8": case "10": case "11": case "12": gestionar(mascota, opcion); break;
                case "9": System.out.println(salud.listarRecordatorios(mascota)); break;
                case "0": break;
                default: entrada.error("opción incorrecta.");
            }
        }
    }

    private void registrarSalud(Mascota mascota, RegistroSanitario registro) {
        String[] tipos = {"Vacuna", "Desparasitación"};
        String tipo = entrada.elegir(tipos);
        String producto = entrada.obligatorio("Producto aplicado");
        LocalDate fecha = entrada.fecha("Fecha de aplicación (AAAA-MM-DD)", false);
        while (fecha.isAfter(LocalDate.now())) {
            entrada.error("la fecha de aplicación no puede ser futura.");
            fecha = entrada.fecha("Ingresa de nuevo la fecha de aplicación (AAAA-MM-DD)", false);
        }
        LocalDate proxima = entrada.fecha("Próxima fecha (AAAA-MM-DD)", true);
        while (proxima != null && !proxima.isAfter(fecha)) {
            entrada.error("la próxima fecha debe ser posterior a la aplicación.");
            proxima = entrada.fecha("Ingresa de nuevo la próxima fecha (AAAA-MM-DD)", true);
        }

        if (registro == null) {
            System.out.println(salud.registrarSalud(mascota, tipo, producto, fecha, proxima));
        } else if (registro.modificarRegistro(tipo, producto, fecha, proxima)) {
            System.out.println("Registro sanitario modificado.");
        } else {
            entrada.error("no se pudo guardar el registro.");
        }
    }

    private void modificarSalud(Mascota mascota) {
        System.out.println(salud.listarHistorial(mascota));
        while (true) {
            String id = entrada.pedir("ID del registro (0 cancelar): ").trim();
            if (id.equals("0")) return;
            RegistroSanitario registro = salud.buscarRegistro(mascota, id);
            if (registro != null) {
                registrarSalud(mascota, registro);
                return;
            }
            entrada.error("el registro no existe para esa mascota.");
        }
    }

    private void programar(Mascota mascota) {
        String[] tipos = {ActividadAgenda.CONSULTA, "Paseo", "Baño", "Vacuna", ActividadAgenda.COMIDA};
        String tipo = entrada.elegir(tipos);
        String nombre = entrada.obligatorio("Nombre de la actividad");
        LocalDate fecha = entrada.fecha("Fecha programada (AAAA-MM-DD)", false);
        LocalTime hora = entrada.hora();
        int frecuencia = entrada.dias("Repetir cada cuántos días (0 para una sola vez): ");
        if (mascota.getAgenda().programarActividad(nombre, tipo, fecha, hora, frecuencia)) {
            System.out.println("Actividad guardada.");
            if (frecuencia > 0) {
                System.out.println("Las siguientes fechas se generan aunque la anterior esté pendiente.");
            }
        } else {
            entrada.error("la actividad ya existe o los datos no son válidos.");
        }
    }

    private void gestionar(Mascota mascota, String opcion) {
        System.out.println(salud.listarAgenda(mascota));
        ActividadAgenda actividad;
        while (true) {
            String id = entrada.pedir("ID de actividad (0 cancelar): ").trim();
            if (id.equals("0")) return;
            actividad = mascota.getAgenda().buscarActividad(id);
            if (actividad != null) break;
            entrada.error("esa actividad no existe.");
        }

        boolean cambio;
        if (opcion.equals("8")) {
            int dias = entrada.dias("Días de anticipación (0 para el mismo día): ");
            cambio = actividad.agregarRecordatorio(dias);
        } else if (opcion.equals("10")) {
            if (ActividadAgenda.COMIDA.equals(actividad.getCategoria())) {
                System.out.println("Esta comida se marcará cuando se registre su consumo.");
                System.out.println("El registro de consumos no forma parte de esta entrega.");
                return;
            }
            String responsable = entrada.pedir("Responsable (opcional): ").trim();
            cambio = actividad.marcarRealizada(LocalDateTime.now(), responsable);
        } else if (opcion.equals("11")) {
            cambio = actividad.cancelar();
        } else {
            cambio = actividad.deshacerRegistro();
        }

        if (cambio) System.out.println("Cambio guardado.");
        else entrada.error("revisa el estado del evento o si el recordatorio ya existe.");
    }
}
