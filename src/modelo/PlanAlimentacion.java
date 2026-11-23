package modelo;

import java.time.LocalDate;

public class PlanAlimentacion {
    private final String id;
    private final Mascota mascota;
    private final Producto producto;
    private final double cantidadDiariaGramos;
    private final int numeroComidas;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;

    public PlanAlimentacion(String id, Mascota mascota, Producto producto,
                            double gramosDiarios, int comidas, LocalDate inicio, LocalDate fin) {
        this.id = id;
        this.mascota = mascota;
        this.producto = producto;
        this.cantidadDiariaGramos = gramosDiarios;
        this.numeroComidas = comidas;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    public String getId() { return id; }
    public Mascota getMascota() { return mascota; }
    public Producto getProducto() { return producto; }
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

    public double calcularGramosPorPorcion() {
        return cantidadDiariaGramos / numeroComidas;
    }

    public double calcularDiasEstimados() {
        return producto.getGramosDisponibles() / cantidadDiariaGramos;
    }

    public LocalDate calcularFechaAgotamiento(LocalDate fechaCalculo) {
        if (fechaCalculo == null || producto == null || cantidadDiariaGramos <= 0) {
            return null;
        }
        double dias = Math.ceil(calcularDiasEstimados());
        // Evitamos fechas imposibles sin lanzar una excepcion.
        if (!Double.isFinite(dias) || dias < 0 || dias > 365000) return null;
        long diasEnteros = (long) dias;
        if (fechaCalculo.isAfter(LocalDate.MAX.minusDays(diasEnteros))) return null;
        return fechaCalculo.plusDays(diasEnteros);
    }
}
