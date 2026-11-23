package interfaz;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import negocio.GestorPerfiles;
import negocio.Validaciones;

// Pide datos y muestra errores. No guarda propietarios, mascotas ni planes.
public class EntradaConsola {
    private Scanner teclado;
    private GestorPerfiles perfiles;

    public EntradaConsola(Scanner teclado, GestorPerfiles perfiles) {
        this.teclado = teclado;
        this.perfiles = perfiles;
    }

    public String pedir(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine();
    }

    public void error(String mensaje) {
        System.out.println("Error: " + mensaje);
    }

    public String obligatorio(String campo) {
        String texto = pedir(campo + ": ");
        while (!Validaciones.campoObligatorio(texto)) {
            error(campo + " es obligatorio.");
            texto = pedir("Ingresa de nuevo " + campo + ": ");
        }
        return texto.trim();
    }

    public String correo(String idActual) {
        String correo = pedir("Correo: ");
        while (true) {
            if (!Validaciones.validarCorreo(correo)) {
                error("correo incorrecto. Ejemplo: usuario@correo.com");
            } else if (perfiles.correoRegistrado(correo, idActual)) {
                error("ese correo ya esta registrado.");
            } else {
                return correo.trim();
            }
            correo = pedir("Ingresa de nuevo el correo: ");
        }
    }

    public String telefono() {
        String telefono = pedir("Teléfono (10 dígitos): ");
        while (!Validaciones.validarTelefono(telefono)) {
            error("el teléfono debe tener exactamente 10 números.");
            telefono = pedir("Ingresa de nuevo el teléfono: ");
        }
        return telefono;
    }

    public String especie() {
        String especie = pedir("Especie (perro/gato): ");
        while (!Validaciones.validarEspecie(especie)) {
            error("la especie solo puede ser perro o gato.");
            especie = pedir("Ingresa de nuevo la especie: ");
        }
        return especie.trim();
    }

    public String idPropietario() {
        String id = pedir("ID del propietario (0 para cancelar): ").trim();
        while (perfiles.buscarPropietario(id) == null) {
            if (id.equals("0")) return null;
            error("ese propietario no existe.");
            id = pedir("Ingresa de nuevo el ID del propietario (0 para cancelar): ").trim();
        }
        return id;
    }

    public String idMascota() {
        String id = pedir("ID de la mascota (0 para cancelar): ").trim();
        while (perfiles.buscarMascota(id) == null) {
            if (id.equals("0")) return null;
            error("esa mascota no existe.");
            id = pedir("Ingresa de nuevo el ID de la mascota (0 para cancelar): ").trim();
        }
        return id;
    }

    public LocalDate nacimiento() {
        while (true) {
            LocalDate fecha = fecha("Fecha de nacimiento (AAAA-MM-DD)", false);
            if (Validaciones.validarFechaNacimiento(fecha)) return fecha;
            error("la fecha de nacimiento no puede ser futura.");
        }
    }

    public LocalDate fecha(String campo, boolean opcional) {
        while (true) {
            String pregunta = campo + ": ";
            if (opcional) {
                pregunta = campo + " (Enter para dejar vacío): ";
            }
            String texto = pedir(pregunta).trim();
            if (opcional && texto.isEmpty()) return null;
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                error("ingresa una fecha válida en formato AAAA-MM-DD.");
            }
        }
    }

    // Reutilizamos un único método para todos los números positivos.
    public double positivo(String campo) {
        while (true) {
            String texto = pedir(campo + ": ").trim().replace(',', '.');
            try {
                double numero = Double.parseDouble(texto);
                if (Validaciones.numeroPositivo(numero)) return numero;
            } catch (NumberFormatException e) {
                // Si el texto no es numérico, se informa debajo.
            }
            error("ingresa un número mayor que cero (por ejemplo 12.5).");
        }
    }

    public int entero(String campo, int minimo, int maximo) {
        while (true) {
            try {
                int numero = Integer.parseInt(pedir(campo + ": ").trim());
                if (numero >= minimo && numero <= maximo) return numero;
            } catch (NumberFormatException e) {
                // No permite letras ni decimales cuando se espera un entero.
            }
            error("ingresa un número entero entre " + minimo + " y " + maximo + ".");
        }
    }

    public int dias(String mensaje) {
        while (true) {
            try {
                int dias = Integer.parseInt(pedir(mensaje).trim());
                if (dias >= 0) return dias;
            } catch (NumberFormatException e) {
                // Vuelve a pedir el dato.
            }
            error("ingresa un número entero de días igual o mayor que cero.");
        }
    }

    public LocalTime hora() {
        while (true) {
            try {
                return LocalTime.parse(pedir("Hora (HH:MM): ").trim());
            } catch (DateTimeParseException e) {
                error("ingresa una hora válida en formato HH:MM.");
            }
        }
    }

    public LocalDate finPlan(LocalDate inicio) {
        while (true) {
            LocalDate fin = fecha("Fin del plan (AAAA-MM-DD)", true);
            if (fin == null || fin.isAfter(inicio)) return fin;
            error("el fin debe ser posterior al inicio.");
        }
    }

    public LocalDate cierrePlan(LocalDate inicio, LocalDate finAnterior) {
        while (true) {
            String texto = pedir("Primer día fuera de vigencia (AAAA-MM-DD, 0 cancelar): ").trim();
            if (texto.equals("0")) return null;
            try {
                LocalDate fin = LocalDate.parse(texto);
                if (fin.isAfter(inicio) && (finAnterior == null || fin.isBefore(finAnterior))) return fin;
            } catch (DateTimeParseException e) {
                // Mensaje único para fecha inválida o cierre que no corresponde.
            }
            error("el cierre debe ser posterior al inicio y anterior al fin existente.");
        }
    }

    public String elegir(String[] opciones) {
        for (int i = 0; i < opciones.length; i++) {
            System.out.println((i + 1) + ". " + opciones[i]);
        }
        return opciones[entero("Tipo", 1, opciones.length) - 1];
    }
}
