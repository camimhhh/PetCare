package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import modelo.ActividadAgenda;
import modelo.Mascota;


public class DatosDemostracion {

    public static void cargar(GestorPerfiles perfiles, GestorPlanes planes,
                              GestorSaludAgenda salud) {
        LocalDate hoy = LocalDate.now();

        // MODULO 1: dos propietarios y tres mascotas.
        perfiles.registrarPropietario("Pedro", "Perez", "pedro@gmail.com", "0987654321");
        perfiles.registrarPropietario("Carlos", "Torres", "carlitos@gmail.com", "0991122334");

        perfiles.registrarMascota("P1", "Toby", "perro", "Mestizo", "Macho",
                hoy.minusYears(3), 12.5);
        perfiles.registrarMascota("P1", "Luna", "gato", "Europeo", "Hembra",
                hoy.minusYears(2), 3.8);
        perfiles.registrarMascota("P2", "Max", "perro", "Labrador", "Macho",
                hoy.minusYears(4), 24.0);

        // MODULO 2: Toby conserva un plan alimenticio antiguo y uno actual.
        // Ambos usan el MISMO alimento, sin duplicar sus existencias.
        planes.registrarAlimentacion("M1", "Balanceado Toby", 6.0, 350, 2,
                hoy.minusDays(60), hoy.minusDays(15));
        planes.registrarAlimentacion("M1", "Balanceado Toby", 0, 300, 2,
                hoy.minusDays(15), null);
        // Luna: 4,5 kg / 100 g al día = 45 días = 1 mes y 15 días (aprox.).
        planes.registrarAlimentacion("M2", "Alimento Luna", 4.5, 100, 2,
                hoy.minusDays(5), null);
        planes.registrarAlimentacion("M3", "Balanceado Max", 8.0, 400, 2,
                hoy.minusDays(5), null);

        // Dos ejercicios vigentes para Toby: 150 + 60 = 210 minutos por semana.
        planes.registrarActividad("M1", "Paseo", 30, 5, hoy.minusDays(7), null);
        planes.registrarActividad("M1", "Juego con pelota", 20, 3, hoy.minusDays(7), null);
        planes.registrarActividad("M2", "Juego con juguete", 15, 4, hoy.minusDays(7), null);
        planes.registrarActividad("M3", "Caminata", 45, 4, hoy.minusDays(7), null);

        // MODULO 3: vacunas, desparasitaciones y próximas fechas.
        Mascota toby = perfiles.buscarMascota("M1");
        Mascota luna = perfiles.buscarMascota("M2");
        Mascota max = perfiles.buscarMascota("M3");

        salud.registrarSalud(toby, "Vacuna", "Antirrabica",
                hoy.minusDays(200), hoy.plusDays(165));
        salud.registrarSalud(toby, "Desparasitación", "Antiparasitario",
                hoy.minusDays(75), hoy.plusDays(15));
        salud.registrarSalud(luna, "Vacuna", "Triple felina",
                hoy.minusDays(100), hoy.plusDays(265));
        salud.registrarSalud(max, "Vacuna", "Sextuple",
                hoy.minusDays(150), hoy.plusDays(215));

        // Cita futura con aviso activo desde ayer: se ve en 'Recordatorios'.
        toby.getAgenda().programarActividad("Control veterinario de Toby",
                ActividadAgenda.CONSULTA, hoy.plusDays(1), LocalTime.of(10, 0));
        ActividadAgenda citaFutura = toby.getAgenda().buscarActividad("AG-M1-1");
        if (citaFutura != null) citaFutura.agregarRecordatorio(2);

        // Cita anterior que ya se cumplió: se ve en 'Citas realizadas'.
        toby.getAgenda().programarActividad("Revision veterinaria anterior",
                ActividadAgenda.CONSULTA, hoy.minusDays(7), LocalTime.of(15, 0));
        ActividadAgenda citaPasada = toby.getAgenda().buscarActividad("AG-M1-2");
        if (citaPasada != null) {
            citaPasada.marcarRealizada(LocalDateTime.of(hoy.minusDays(7),
                    LocalTime.of(15, 30)), "Domenica");
        }

        // Actividad terminada que NO es comida; demuestra el cambio de estado.
        toby.getAgenda().programarActividad("Bano de Toby", "Baño",
                hoy.minusDays(1), LocalTime.of(16, 0));
        ActividadAgenda bano = toby.getAgenda().buscarActividad("AG-M1-3");
        if (bano != null) {
            bano.marcarRealizada(LocalDateTime.of(hoy.minusDays(1),
                    LocalTime.of(16, 30)), "Domenica");
        }

        // Paseo cada dos días; su siguiente fecha no depende del anterior.
        toby.getAgenda().programarActividad("Paseo de Toby", "Paseo",
                hoy.minusDays(1), LocalTime.of(18, 0), 2);

        // Comidas diarias: las de ayer y hoy siguen pendientes.
        // No las marcamos como comidas realizadas porque eso requiere RF7.
        toby.getAgenda().programarActividad("Desayuno de Toby",
                ActividadAgenda.COMIDA, hoy.minusDays(2), LocalTime.of(8, 0), 1);

        // LUNA: cita futura, cita realizada y recordatorio activo.
        luna.getAgenda().programarActividad("Control veterinario de Luna",
                ActividadAgenda.CONSULTA, hoy.plusDays(2), LocalTime.of(14, 0));
        ActividadAgenda citaLuna = luna.getAgenda().buscarActividad("AG-M2-1");
        if (citaLuna != null) citaLuna.agregarRecordatorio(3);

        luna.getAgenda().programarActividad("Consulta anterior de Luna",
                ActividadAgenda.CONSULTA, hoy.minusDays(10), LocalTime.of(11, 0));
        ActividadAgenda citaLunaAnterior = luna.getAgenda().buscarActividad("AG-M2-2");
        if (citaLunaAnterior != null) {
            citaLunaAnterior.marcarRealizada(LocalDateTime.of(hoy.minusDays(10),
                    LocalTime.of(11, 30)), "Domenica");
        }

        // MAX: también tiene las tres opciones de consulta con ejemplos.
        max.getAgenda().programarActividad("Vacunacion de Max", "Vacuna",
                hoy.plusDays(3), LocalTime.of(11, 0));
        max.getAgenda().programarActividad("Control veterinario de Max",
                ActividadAgenda.CONSULTA, hoy.plusDays(1), LocalTime.of(10, 30));
        ActividadAgenda citaMax = max.getAgenda().buscarActividad("AG-M3-2");
        if (citaMax != null) citaMax.agregarRecordatorio(2);

        max.getAgenda().programarActividad("Consulta anterior de Max",
                ActividadAgenda.CONSULTA, hoy.minusDays(6), LocalTime.of(9, 0));
        ActividadAgenda citaMaxAnterior = max.getAgenda().buscarActividad("AG-M3-3");
        if (citaMaxAnterior != null) {
            citaMaxAnterior.marcarRealizada(LocalDateTime.of(hoy.minusDays(6),
                    LocalTime.of(9, 30)), "Camila");
        }


    }
}
