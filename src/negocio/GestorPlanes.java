package negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Mascota;
import modelo.PlanActividad;
import modelo.PlanAlimentacion;
import modelo.Producto;

// Coordina los planes de alimentacion (RF3) y actividad fisica (RF4).
public class GestorPlanes {
    private GestorPerfiles perfiles;
    private ArrayList<PlanAlimentacion> alimentacion = new ArrayList<>();
    private ArrayList<PlanActividad> actividad = new ArrayList<>();
    private int siguienteAlimentacion = 1;
    private int siguienteActividad = 1;
    private int siguienteProducto = 1;

    public GestorPlanes(GestorPerfiles perfiles) {
        this.perfiles = perfiles;
    }

    public Producto buscarProducto(String idMascota, String nombre) {
        Mascota mascota = perfiles.buscarMascota(idMascota);
        if (mascota == null) return null;
        return mascota.buscarProducto(nombre);
    }

    // Si los datos no son correctos, devuelve null y no guarda el producto.
    public Producto registrarProducto(String idMascota, String nombre, double kilos) {
        Mascota mascota = perfiles.buscarMascota(idMascota);
        if (mascota == null || !Validaciones.campoObligatorio(nombre)
                || !Validaciones.numeroPositivo(kilos)
                || !Validaciones.numeroPositivo(kilos * 1000)
                || mascota.buscarProducto(nombre) != null) {
            return null;
        }

        Producto producto = new Producto("PR" + siguienteProducto, mascota, nombre.trim(), kilos * 1000);
        if (!mascota.agregarProducto(producto)) return null;
        siguienteProducto++;
        return producto;
    }

    // Devuelve null si las fechas son validas, o un mensaje si hay un problema.
    // Asi podemos explicar el error sin lanzar excepciones.
    public String comprobarVigenciaAlimentacion(String idMascota, LocalDate inicio, LocalDate fin) {
        Mascota mascota = perfiles.buscarMascota(idMascota);
        if (mascota == null) return "la mascota no esta registrada.";
        if (!fechasValidas(inicio, fin)) {
            return "la fecha de fin debe ser posterior a la fecha de inicio.";
        }
        for (PlanAlimentacion plan : alimentacion) {
            if (plan.getMascota() == mascota
                    && (fin == null || plan.getFechaInicio().isBefore(fin))
                    && (plan.getFechaFin() == null || inicio.isBefore(plan.getFechaFin()))) {
                return "las fechas se superponen con el plan " + plan.getId() + ".";
            }
        }
        return null;
    }

    // Solo se guarda el alimento nuevo cuando el plan tambien es valido.
    public PlanAlimentacion registrarAlimentacion(String idMascota, String nombreAlimento,
                                                  double kilosSiEsNuevo, double gramosDiarios,
                                                  int comidas, LocalDate inicio, LocalDate fin) {
        Mascota mascota = perfiles.buscarMascota(idMascota);
        if (mascota == null || comprobarVigenciaAlimentacion(idMascota, inicio, fin) != null
                || !Validaciones.campoObligatorio(nombreAlimento)
                || !Validaciones.numeroPositivo(gramosDiarios)
                || comidas < 1 || comidas > 6) {
            return null;
        }

        Producto producto = mascota.buscarProducto(nombreAlimento);
        boolean productoNuevo = producto == null;
        if (productoNuevo) {
            if (!Validaciones.numeroPositivo(kilosSiEsNuevo)
                    || !Validaciones.numeroPositivo(kilosSiEsNuevo * 1000)) {
                return null;
            }
            producto = new Producto("PR" + siguienteProducto, mascota,
                    nombreAlimento.trim(), kilosSiEsNuevo * 1000);
        }

        PlanAlimentacion plan = new PlanAlimentacion("PA" + siguienteAlimentacion,
                mascota, producto, gramosDiarios, comidas, inicio, fin);

        LocalDate fechaCalculo = inicio;
        if (LocalDate.now().isAfter(inicio)) fechaCalculo = LocalDate.now();
        if (plan.calcularFechaAgotamiento(fechaCalculo) == null) return null;

        if (productoNuevo) {
            if (!mascota.agregarProducto(producto)) return null;
            siguienteProducto++;
        }
        alimentacion.add(plan);
        siguienteAlimentacion++;
        return plan;
    }

    public PlanActividad registrarActividad(String idMascota, String tipo, int minutos,
                                             int frecuencia, LocalDate inicio, LocalDate fin) {
        Mascota mascota = perfiles.buscarMascota(idMascota);
        if (mascota == null || !fechasValidas(inicio, fin)
                || !Validaciones.campoObligatorio(tipo)
                || minutos <= 0 || frecuencia < 1 || frecuencia > 7) {
            return null;
        }
        PlanActividad plan = new PlanActividad("PE" + siguienteActividad, mascota,
                tipo.trim(), minutos, frecuencia, inicio, fin);
        actividad.add(plan);
        siguienteActividad++;
        return plan;
    }

    private boolean fechasValidas(LocalDate inicio, LocalDate fin) {
        return inicio != null && (fin == null || fin.isAfter(inicio));
    }

    // Ordenamos utilizando for y while, sin Comparator.
    public ArrayList<PlanAlimentacion> historialAlimentacion(String idMascota) {
        Mascota mascota = perfiles.buscarMascota(idMascota);
        ArrayList<PlanAlimentacion> historial = new ArrayList<>();
        if (mascota == null) return historial;

        for (PlanAlimentacion plan : alimentacion) {
            if (plan.getMascota() == mascota) {
                int posicion = 0;
                while (posicion < historial.size()
                        && !historial.get(posicion).getFechaInicio().isAfter(plan.getFechaInicio())) {
                    posicion++;
                }
                historial.add(posicion, plan);
            }
        }
        return historial;
    }

    public ArrayList<PlanActividad> historialActividad(String idMascota) {
        Mascota mascota = perfiles.buscarMascota(idMascota);
        ArrayList<PlanActividad> historial = new ArrayList<>();
        if (mascota == null) return historial;

        for (PlanActividad plan : actividad) {
            if (plan.getMascota() == mascota) {
                int posicion = 0;
                while (posicion < historial.size()
                        && !historial.get(posicion).getFechaInicio().isAfter(plan.getFechaInicio())) {
                    posicion++;
                }
                historial.add(posicion, plan);
            }
        }
        return historial;
    }

    public long totalMinutosSemanales(String idMascota, LocalDate fecha) {
        if (fecha == null) return 0;
        long total = 0;
        for (PlanActividad plan : historialActividad(idMascota)) {
            if (plan.estaVigente(fecha)) total += plan.calcularMinutosSemanales();
        }
        return total;
    }
}
