package negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Propietario;
import modelo.Mascota;

// Coordina RF1 y RF2. NO lee teclado ni muestra ventanas.
public class GestorPerfiles {
    private ArrayList<Propietario> propietarios;
    private int siguienteIdPropietario;
    private int siguienteIdMascota;

    public GestorPerfiles() {
        propietarios = new ArrayList<>();
        siguienteIdPropietario = 1;
        siguienteIdMascota = 1;
    }

    public boolean correoRegistrado(String correo, String idQueSeModifica) {
        if (correo == null) {
            return false;
        }
        for (Propietario propietario : propietarios) {
            if (propietario.getCorreo().equalsIgnoreCase(correo.trim())) {
                // Al modificar se permite conservar el propio correo.
                if (idQueSeModifica == null
                        || !propietario.getId().equalsIgnoreCase(idQueSeModifica)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String registrarPropietario(String nombres, String apellidos,
                                      String correo, String telefono) {
        if (!Validaciones.campoObligatorio(nombres)
                || !Validaciones.campoObligatorio(apellidos)
                || !Validaciones.campoObligatorio(correo)
                || !Validaciones.campoObligatorio(telefono)) {
            return "Error: todos los datos son obligatorios.";
        }
        if (!Validaciones.validarCorreo(correo)) {
            return "Error: el correo no tiene un formato valido.";
        }
        if (correoRegistrado(correo, null)) {
            return "Error: el correo ya esta registrado.";
        }
        if (!Validaciones.validarTelefono(telefono)) {
            return "Error: el telefono debe contener 10 digitos numericos.";
        }
        Propietario propietario = new Propietario("P" + siguienteIdPropietario,
                nombres.trim(), apellidos.trim(), correo.trim(), telefono);
        propietarios.add(propietario);
        siguienteIdPropietario++;
        return "Propietario registrado correctamente. ID: " + propietario.getId();
    }

    public Propietario buscarPropietario(String id) {
        if (id == null) {
            return null;
        }
        for (Propietario propietario : propietarios) {
            if (propietario.getId().equalsIgnoreCase(id.trim())) {
                return propietario;
            }
        }
        return null;
    }

    public String modificarPropietario(String id, String nombres, String apellidos,
                                      String correo, String telefono) {
        Propietario propietario = buscarPropietario(id);
        if (propietario == null) {
            return "Error: el propietario no existe.";
        }
        if (!Validaciones.campoObligatorio(nombres)
                || !Validaciones.campoObligatorio(apellidos)
                || !Validaciones.campoObligatorio(correo)
                || !Validaciones.campoObligatorio(telefono)) {
            return "Error: todos los datos son obligatorios.";
        }
        if (!Validaciones.validarCorreo(correo)) {
            return "Error: correo no valido.";
        }
        if (correoRegistrado(correo, id)) {
            return "Error: el correo ya pertenece a otro propietario.";
        }
        if (!Validaciones.validarTelefono(telefono)) {
            return "Error: telefono incorrecto.";
        }
        propietario.modificarDatos(nombres.trim(), apellidos.trim(), correo.trim(), telefono);
        return "Propietario modificado correctamente.";
    }

    public String registrarMascota(String idPropietario, String nombre, String especie,
                                  String raza, String sexo, LocalDate fechaNacimiento,
                                  double peso) {
        Propietario propietario = buscarPropietario(idPropietario);
        if (propietario == null) {
            return "Error: el propietario no existe.";
        }
        String error = comprobarMascota(nombre, especie, raza, sexo, fechaNacimiento, peso);
        if (error != null) {
            return error;
        }
        Mascota mascota = new Mascota("M" + siguienteIdMascota,
                nombre.trim(), especie.trim(), raza.trim(), sexo.trim(),
                fechaNacimiento, peso, propietario);
        if (!propietario.registrarMascota(mascota)) {
            return "Error: no se pudo asociar la mascota.";
        }
        siguienteIdMascota++;
        return "Mascota registrada correctamente. ID: " + mascota.getId();
    }

    private String comprobarMascota(String nombre, String especie, String raza,
                                   String sexo, LocalDate fecha, double peso) {
        if (!Validaciones.campoObligatorio(nombre)
                || !Validaciones.campoObligatorio(raza)
                || !Validaciones.campoObligatorio(sexo)) {
            return "Error: nombre, raza y sexo son obligatorios.";
        }
        if (!Validaciones.validarEspecie(especie)) {
            return "Error: la especie debe ser perro o gato.";
        }
        if (!Validaciones.validarFechaNacimiento(fecha)) {
            return "Error: la fecha de nacimiento no puede ser futura.";
        }
        if (!Validaciones.validarPeso(peso)) {
            return "Error: el peso debe ser mayor que cero.";
        }
        return null;
    }

    public Mascota buscarMascota(String idMascota) {
        if (idMascota == null) {
            return null;
        }
        for (Propietario propietario : propietarios) {
            Mascota encontrada = propietario.buscarMascota(idMascota);
            if (encontrada != null) {
                return encontrada;
            }
        }
        return null;
    }

    public String modificarMascota(String idMascota, String nombre, String especie,
                                  String raza, String sexo, LocalDate fechaNacimiento,
                                  double peso) {
        Mascota mascota = buscarMascota(idMascota);
        if (mascota == null) {
            return "Error: la mascota no existe.";
        }
        String error = comprobarMascota(nombre, especie, raza, sexo, fechaNacimiento, peso);
        if (error != null) {
            return error;
        }
        mascota.modificarDatos(nombre.trim(), especie.trim(), raza.trim(),
                sexo.trim(), fechaNacimiento, peso);
        return "Mascota modificada correctamente.";
    }

    public String listarPropietarios() {
        if (propietarios.isEmpty()) {
            return "No hay propietarios registrados.";
        }
        String lista = "PROPIETARIOS\n";
        for (Propietario propietario : propietarios) {
            lista += propietario.getId() + " - " + propietario.getNombreCompleto()
                    + " | " + propietario.getCorreo() + " | "
                    + propietario.getTelefono() + "\n";
        }
        return lista;
    }

    public String listarMascotas(String idPropietario) {
        Propietario propietario = buscarPropietario(idPropietario);
        if (propietario == null) {
            return "Error: el propietario no existe.";
        }
        if (propietario.getMascotas().isEmpty()) {
            return "Ese propietario no tiene mascotas registradas.";
        }
        String lista = "MASCOTAS DE " + propietario.getNombreCompleto() + "\n";
        for (Mascota mascota : propietario.getMascotas()) {
            lista += mascota.getId() + " - " + mascota.getNombre()
                    + " | " + mascota.getEspecie()
                    + " | " + mascota.calcularEdad() + " años"
                    + " | " + mascota.getPeso() + " kg\n";
        }
        return lista;
    }

    public String detalleMascota(String idMascota) {
        Mascota mascota = buscarMascota(idMascota);
        if (mascota == null) {
            return "Error: la mascota no existe.";
        }
        return "ID: " + mascota.getId()
                + "\nNombre: " + mascota.getNombre()
                + "\nEspecie: " + mascota.getEspecie()
                + "\nRaza: " + mascota.getRaza()
                + "\nSexo: " + mascota.getSexo()
                + "\nNacimiento: " + mascota.getFechaNacimiento()
                + "\nEdad: " + mascota.calcularEdad() + " años"
                + "\nPeso: " + mascota.getPeso() + " kg"
                + "\nPropietario: " + mascota.getPropietario().getNombreCompleto();
    }
}
