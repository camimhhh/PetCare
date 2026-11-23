package negocio;

import java.time.LocalDate;

// Clase de ayuda: comprueba datos, pero NO pide nada al usuario.
public class Validaciones {

    // static: permite usar Validaciones.campoObligatorio(...) directamente.
    public static boolean campoObligatorio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    // Comprobacion BASICA de correo: un @, un punto despues y sin espacios.
    public static boolean validarCorreo(String correo) {
        if (!campoObligatorio(correo) || correo.contains(" ")) {
            return false;
        }
        int arroba = correo.indexOf('@');
        int punto = correo.lastIndexOf('.');
        return arroba > 0 && arroba == correo.lastIndexOf('@')
                && punto > arroba + 1 && punto < correo.length() - 1;
    }

    // El telefono es String para permitir ceros al inicio.
    public static boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("[0-9]{10}");
    }

    public static boolean validarEspecie(String especie) {
        return especie != null
                && (especie.trim().equalsIgnoreCase("perro")
                    || especie.trim().equalsIgnoreCase("gato"));
    }

    public static boolean validarFechaNacimiento(LocalDate fecha) {
        return fecha != null && !fecha.isAfter(LocalDate.now());
    }

    public static boolean validarPeso(double peso) {
        return peso > 0 && Double.isFinite(peso);
    }
}
