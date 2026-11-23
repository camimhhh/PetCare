package interfaz;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import negocio.GestorPerfiles;
import negocio.Validaciones;

// Solo se encarga de mostrar el menu y pedir datos al usuario.
public class MenuConsola {
    private Scanner teclado;
    private GestorPerfiles gestor;

    public MenuConsola(GestorPerfiles gestor) {
        this.gestor = gestor;
        this.teclado = new Scanner(System.in);
    }

    private String pedir(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine();
    }

    // Muestra el error en consola, sin ventanas emergentes.
    private void mostrarError(String mensaje) {
        System.out.println("Error: " + mensaje);
    }

    public void iniciar() {
        String opcion = "";
        while (!opcion.equals("0")) {
            System.out.println("\n=== PETCARE - MODULO 1 ===");
            System.out.println("1. Registrar propietario");
            System.out.println("2. Consultar propietarios");
            System.out.println("3. Modificar propietario");
            System.out.println("4. Registrar mascota");
            System.out.println("5. Consultar mascotas de un propietario");
            System.out.println("6. Buscar mascota por ID");
            System.out.println("7. Modificar mascota");
            System.out.println("0. Salir");
            opcion = pedir("Opcion: ");

            switch (opcion) {
                case "1": registrarPropietario(); break;
                case "2": System.out.println(gestor.listarPropietarios()); break;
                case "3": modificarPropietario(); break;
                case "4": registrarMascota(); break;
                case "5": mostrarMascotas(); break;
                case "6": mostrarMascota(); break;
                case "7": modificarMascota(); break;
                case "0": System.out.println("Hasta pronto."); break;
                default: mostrarError("opcion incorrecta.");
            }
        }
    }

    // Cada metodo se queda en SU CAMPO hasta recibir un dato correcto.
    private String pedirObligatorio(String nombreCampo) {
        String dato = pedir(nombreCampo + ": ");
        while (!Validaciones.campoObligatorio(dato)) {
            mostrarError(nombreCampo + " es obligatorio.");
            dato = pedir("Ingresa de nuevo " + nombreCampo + ": ");
        }
        return dato.trim();
    }

    private String pedirCorreo(String idQueSeModifica) {
        String correo = pedir("Correo: ");
        boolean valido = false;
        while (!valido) {
            if (!Validaciones.validarCorreo(correo)) {
                mostrarError("correo incorrecto. Ejemplo: usuario@correo.com");
            } else if (gestor.correoRegistrado(correo, idQueSeModifica)) {
                mostrarError("ese correo ya esta registrado.");
            } else {
                valido = true;
            }
            if (!valido) {
                correo = pedir("Ingresa de nuevo el correo: ");
            }
        }
        return correo.trim();
    }

    private String pedirTelefono() {
        String telefono = pedir("Telefono (10 digitos): ");
        while (!Validaciones.validarTelefono(telefono)) {
            mostrarError("el telefono debe tener exactamente 10 numeros.");
            telefono = pedir("Ingresa de nuevo el telefono: ");
        }
        return telefono;
    }

    private String pedirEspecie() {
        String especie = pedir("Especie (perro/gato): ");
        while (!Validaciones.validarEspecie(especie)) {
            mostrarError("la especie solo puede ser perro o gato.");
            especie = pedir("Ingresa de nuevo la especie: ");
        }
        return especie.trim();
    }

    private LocalDate pedirNacimiento() {
        String texto = pedir("Fecha de nacimiento (AAAA-MM-DD): ");
        while (true) {
            try {
                LocalDate fecha = LocalDate.parse(texto);
                if (Validaciones.validarFechaNacimiento(fecha)) {
                    return fecha;
                }
                mostrarError("la fecha no puede ser futura.");
            } catch (DateTimeParseException e) {
                mostrarError("escribe una fecha valida con formato AAAA-MM-DD.");
            }
            texto = pedir("Ingresa de nuevo la fecha (AAAA-MM-DD): ");
        }
    }

    private double pedirPeso() {
        String texto = pedir("Peso en kg (ejemplo 12.5): ");
        while (true) {
            try {
                double peso = Double.parseDouble(texto);
                if (Validaciones.validarPeso(peso)) {
                    return peso;
                }
                mostrarError("el peso debe ser mayor que cero.");
            } catch (NumberFormatException e) {
                mostrarError("el peso debe ser un numero. Ejemplo: 12.5");
            }
            texto = pedir("Ingresa de nuevo el peso: ");
        }
    }

    // Deja cancelar con 0, sin obligar a reiniciar el menu.
    private String pedirIdPropietario() {
        String id = pedir("ID del propietario (0 para cancelar): ");
        while (gestor.buscarPropietario(id) == null) {
            if (id.equals("0")) {
                return null;
            }
            mostrarError("ese propietario no existe.");
            id = pedir("Ingresa de nuevo el ID del propietario (0 cancelar): ");
        }
        return id;
    }

    private String pedirIdMascota() {
        String id = pedir("ID de la mascota (0 para cancelar): ");
        while (gestor.buscarMascota(id) == null) {
            if (id.equals("0")) {
                return null;
            }
            mostrarError("esa mascota no existe.");
            id = pedir("Ingresa de nuevo el ID de mascota (0 cancelar): ");
        }
        return id;
    }

    private void registrarPropietario() {
        System.out.println("\n--- REGISTRAR PROPIETARIO ---");
        String nombres = pedirObligatorio("Nombres");
        String apellidos = pedirObligatorio("Apellidos");
        String correo = pedirCorreo(null);
        String telefono = pedirTelefono();
        System.out.println(gestor.registrarPropietario(nombres, apellidos, correo, telefono));
    }

    private void modificarPropietario() {
        String id = pedirIdPropietario();
        if (id == null) return;
        System.out.println("--- MODIFICAR PROPIETARIO ---");
        String nombres = pedirObligatorio("Nombres");
        String apellidos = pedirObligatorio("Apellidos");
        String correo = pedirCorreo(id);
        String telefono = pedirTelefono();
        System.out.println(gestor.modificarPropietario(id, nombres, apellidos, correo, telefono));
    }

    private void registrarMascota() {
        System.out.println("\n--- REGISTRAR MASCOTA ---");
        String id = pedirIdPropietario();
        if (id == null) return;
        String nombre = pedirObligatorio("Nombre de mascota");
        String especie = pedirEspecie();
        String raza = pedirObligatorio("Raza");
        String sexo = pedirObligatorio("Sexo");
        LocalDate nacimiento = pedirNacimiento();
        double peso = pedirPeso();
        System.out.println(gestor.registrarMascota(id, nombre, especie, raza, sexo, nacimiento, peso));
    }

    private void modificarMascota() {
        String id = pedirIdMascota();
        if (id == null) return;
        System.out.println("--- MODIFICAR MASCOTA ---");
        String nombre = pedirObligatorio("Nombre de mascota");
        String especie = pedirEspecie();
        String raza = pedirObligatorio("Raza");
        String sexo = pedirObligatorio("Sexo");
        LocalDate nacimiento = pedirNacimiento();
        double peso = pedirPeso();
        System.out.println(gestor.modificarMascota(id, nombre, especie, raza, sexo, nacimiento, peso));
    }

    private void mostrarMascotas() {
        String id = pedirIdPropietario();
        if (id != null) System.out.println(gestor.listarMascotas(id));
    }

    private void mostrarMascota() {
        String id = pedirIdMascota();
        if (id != null) System.out.println(gestor.detalleMascota(id));
    }
}
