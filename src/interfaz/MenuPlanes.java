package interfaz;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import modelo.Mascota;
import modelo.PlanActividad;
import modelo.PlanAlimentacion;
import modelo.Producto;
import negocio.GestorPerfiles;
import negocio.GestorPlanes;

// Opciones 8 a 13: RF3 y RF4, los planes de alimentación y ejercicio.
public class MenuPlanes {
    private EntradaConsola entrada;
    private GestorPerfiles perfiles;
    private GestorPlanes planes;

    public MenuPlanes(EntradaConsola entrada, GestorPerfiles perfiles, GestorPlanes planes) {
        this.entrada = entrada;
        this.perfiles = perfiles;
        this.planes = planes;
    }

    public void ejecutar(String opcion) {
        switch (opcion) {
            case "8": registrarAlimentacion(); break;
            case "9": verAlimentacion(); break;
            case "10": cerrarAlimentacion(); break;
            case "11": registrarActividad(); break;
            case "12": verActividad(); break;
            case "13": cerrarActividad(); break;
        }
    }

    private void registrarAlimentacion() {
        String id = entrada.idMascota();
        if (id == null) return;

        LocalDate inicio;
        LocalDate fin;
        while (true) {
            String texto = entrada.pedir("Inicio (AAAA-MM-DD, 0 cancelar): ").trim();
            if (texto.equals("0")) return;
            try {
                inicio = LocalDate.parse(texto);
                fin = entrada.finPlan(inicio);
                String error = planes.comprobarVigenciaAlimentacion(id, inicio, fin);
                if (error == null) break;
                entrada.error(error);
                System.out.println("Corrige las fechas o cierra el plan anterior desde el menú.");
            } catch (DateTimeParseException e) {
                entrada.error("fecha incorrecta. Escribe AAAA-MM-DD.");
            }
        }

        // Al volver a usar el mismo alimento NO se crea un segundo producto.
        String nombre = entrada.obligatorio("Nombre del alimento");
        Producto producto = planes.buscarProducto(id, nombre);
        double kilos = 0;
        if (producto == null) {
            kilos = entrada.positivo("Cantidad disponible del alimento en kg");
        } else {
            System.out.println("Se usará el alimento ya registrado: " + producto.getId()
                    + " (" + producto.getGramosDisponibles() + " g disponibles).");
        }

        double gramos = entrada.positivo("Gramos diarios");
        int comidas = entrada.entero("Comidas al día", 1, 6);
        PlanAlimentacion plan = planes.registrarAlimentacion(id, nombre, kilos,
                gramos, comidas, inicio, fin);
        if (plan == null) {
            entrada.error("no se pudo registrar el plan. Revisa los datos y las fechas.");
        } else {
            System.out.println("Plan de alimentación registrado. ID: " + plan.getId());
            imprimirAlimentacion(plan);
        }
    }

    private void verAlimentacion() {
        String id = entrada.idMascota();
        if (id == null) return;
        ArrayList<PlanAlimentacion> lista = planes.historialAlimentacion(id);
        System.out.println("HISTORIAL DE ALIMENTACIÓN: " + perfiles.buscarMascota(id).getNombre());
        if (lista.isEmpty()) System.out.println("No hay planes registrados.");
        for (PlanAlimentacion plan : lista) imprimirAlimentacion(plan);
    }

    private void imprimirAlimentacion(PlanAlimentacion plan) {
        System.out.println(plan.getId() + " | " + plan.getProducto().getNombre() + " ("
                + plan.getProducto().getId() + ") | " + periodo(plan.getFechaInicio(), plan.getFechaFin()));
        System.out.println("Gramos por porción: " + plan.calcularGramosPorPorcion());
        LocalDate hoy = LocalDate.now();
        if (plan.estaVigente(hoy) || hoy.isBefore(plan.getFechaInicio())) {
            LocalDate fechaBase = plan.getFechaInicio();
            if (hoy.isAfter(fechaBase)) fechaBase = hoy;
            LocalDate agotamiento = plan.calcularFechaAgotamiento(fechaBase);
            if (agotamiento == null) {
                entrada.error("no se puede calcular una fecha de agotamiento válida.");
            } else {
                // Un mes se considera aproximadamente de 30 días.
                // Math.ceil cuenta también el último día parcial.
                int diasRedondeados = (int) Math.ceil(plan.calcularDiasEstimados());
                int meses = diasRedondeados / 30;
                int diasRestantes = diasRedondeados % 30;

                String duracion = "";
                if (meses > 0) {
                    if (meses == 1) {
                        duracion = "1 mes";
                    } else {
                        duracion = meses + " meses";
                    }
                }
                if (diasRestantes > 0) {
                    if (meses > 0) {
                        duracion = duracion + " y ";
                    }
                    if (diasRestantes == 1) {
                        duracion = duracion + "1 día";
                    } else {
                        duracion = duracion + diasRestantes + " días";
                    }
                }
                if (diasRedondeados == 0) {
                    duracion = "0 días";
                }
                System.out.println("Duración estimada: " + duracion + " (aprox.)");
                System.out.println("Agotamiento estimado: " + agotamiento);
            }
        }
    }

    private void cerrarAlimentacion() {
        String id = entrada.idMascota();
        if (id == null) return;
        ArrayList<PlanAlimentacion> lista = planes.historialAlimentacion(id);
        if (lista.isEmpty()) {
            System.out.println("No hay planes de alimentación.");
            return;
        }
        for (PlanAlimentacion plan : lista) {
            System.out.println(plan.getId() + " | " + periodo(plan.getFechaInicio(), plan.getFechaFin()));
        }
        while (true) {
            String buscado = entrada.pedir("ID del plan (0 cancelar): ").trim();
            if (buscado.equals("0")) return;
            for (PlanAlimentacion plan : lista) {
                if (plan.getId().equalsIgnoreCase(buscado)) {
                    LocalDate fin = entrada.cierrePlan(plan.getFechaInicio(), plan.getFechaFin());
                    if (fin != null) {
                        if (plan.cerrar(fin)) {
                            System.out.println("Plan cerrado desde: " + fin);
                        } else {
                            entrada.error("la fecha de cierre no es válida.");
                        }
                    }
                    return;
                }
            }
            entrada.error("ese plan no pertenece a la mascota.");
        }
    }

    private void registrarActividad() {
        String id = entrada.idMascota();
        if (id == null) return;
        String tipo = entrada.obligatorio("Tipo de ejercicio");
        int minutos = entrada.entero("Duración (minutos por sesión)", 1, 1440);
        int frecuencia = entrada.entero("Días por semana", 1, 7);
        LocalDate inicio = pedirInicioPlan();
        if (inicio == null) return;
        LocalDate fin = entrada.finPlan(inicio);
        PlanActividad plan = planes.registrarActividad(id, tipo, minutos, frecuencia, inicio, fin);
        if (plan == null) {
            entrada.error("no se pudo registrar el plan. Revisa los datos.");
        } else {
            System.out.println("Plan de actividad registrado. ID: " + plan.getId());
            System.out.println("Minutos semanales: " + plan.calcularMinutosSemanales());
        }
    }

    private LocalDate pedirInicioPlan() {
        while (true) {
            String texto = entrada.pedir("Inicio (AAAA-MM-DD, 0 cancelar): ").trim();
            if (texto.equals("0")) return null;
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                entrada.error("ingresa una fecha válida AAAA-MM-DD.");
            }
        }
    }

    private void verActividad() {
        String id = entrada.idMascota();
        if (id == null) return;
        ArrayList<PlanActividad> lista = planes.historialActividad(id);
        System.out.println("HISTORIAL DE ACTIVIDAD: " + perfiles.buscarMascota(id).getNombre());
        if (lista.isEmpty()) {
            System.out.println("No hay planes registrados.");
            return;
        }
        for (PlanActividad plan : lista) imprimirActividad(plan);
        LocalDate fecha = entrada.fecha("Fecha para sumar minutos (AAAA-MM-DD)", true);
        if (fecha == null) fecha = LocalDate.now();
        System.out.println("Total de minutos semanales en " + fecha + ": "
                + planes.totalMinutosSemanales(id, fecha));
    }

    private void imprimirActividad(PlanActividad plan) {
        System.out.println(plan.getId() + " | " + plan.getTipoActividad() + " | "
                + periodo(plan.getFechaInicio(), plan.getFechaFin()) + " | "
                + plan.calcularMinutosSemanales() + " minutos/semana");
    }

    private void cerrarActividad() {
        String id = entrada.idMascota();
        if (id == null) return;
        ArrayList<PlanActividad> lista = planes.historialActividad(id);
        if (lista.isEmpty()) {
            System.out.println("No hay planes de actividad.");
            return;
        }
        for (PlanActividad plan : lista) imprimirActividad(plan);
        while (true) {
            String buscado = entrada.pedir("ID del plan (0 cancelar): ").trim();
            if (buscado.equals("0")) return;
            for (PlanActividad plan : lista) {
                if (plan.getId().equalsIgnoreCase(buscado)) {
                    LocalDate fin = entrada.cierrePlan(plan.getFechaInicio(), plan.getFechaFin());
                    if (fin != null) {
                        if (plan.cerrar(fin)) {
                            System.out.println("Plan cerrado desde: " + fin);
                        } else {
                            entrada.error("la fecha de cierre no es válida.");
                        }
                    }
                    return;
                }
            }
            entrada.error("ese plan no pertenece a la mascota.");
        }
    }

    private String periodo(LocalDate inicio, LocalDate fin) {
        if (fin == null) {
            return inicio + " a sin fecha de fin";
        }
        return inicio + " a " + fin + " (día fuera del plan)";
    }
}
