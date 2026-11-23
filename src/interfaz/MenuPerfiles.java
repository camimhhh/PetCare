package interfaz;

import java.time.LocalDate;
import negocio.GestorPerfiles;

// Opciones 1 a 7 del módulo 1: RF1 y RF2.
public class MenuPerfiles {
    private EntradaConsola entrada;
    private GestorPerfiles gestor;

    public MenuPerfiles(EntradaConsola entrada, GestorPerfiles gestor) {
        this.entrada = entrada;
        this.gestor = gestor;
    }

    public void ejecutar(String opcion) {
        switch (opcion) {
            case "1": registrarPropietario(); break;
            case "2": System.out.println(gestor.listarPropietarios()); break;
            case "3": modificarPropietario(); break;
            case "4": registrarMascota(); break;
            case "5": consultarMascotas(); break;
            case "6": consultarMascota(); break;
            case "7": modificarMascota(); break;
        }
    }

    private void registrarPropietario() {
        System.out.println("\n--- REGISTRAR PROPIETARIO ---");
        String nombres = entrada.obligatorio("Nombres");
        String apellidos = entrada.obligatorio("Apellidos");
        String correo = entrada.correo(null);
        String telefono = entrada.telefono();
        System.out.println(gestor.registrarPropietario(nombres, apellidos, correo, telefono));
    }

    private void modificarPropietario() {
        String id = entrada.idPropietario();
        if (id == null) return;
        System.out.println("\n--- MODIFICAR PROPIETARIO ---");
        String nombres = entrada.obligatorio("Nombres");
        String apellidos = entrada.obligatorio("Apellidos");
        String correo = entrada.correo(id);
        String telefono = entrada.telefono();
        System.out.println(gestor.modificarPropietario(id, nombres, apellidos, correo, telefono));
    }

    private void registrarMascota() {
        System.out.println("\n--- REGISTRAR MASCOTA ---");
        String id = entrada.idPropietario();
        if (id == null) return;
        String nombre = entrada.obligatorio("Nombre de mascota");
        String especie = entrada.especie();
        String raza = entrada.obligatorio("Raza");
        String sexo = entrada.obligatorio("Sexo");
        LocalDate nacimiento = entrada.nacimiento();
        double peso = entrada.positivo("Peso en kg");
        System.out.println(gestor.registrarMascota(id, nombre, especie, raza, sexo, nacimiento, peso));
    }

    private void modificarMascota() {
        String id = entrada.idMascota();
        if (id == null) return;
        System.out.println("\n--- MODIFICAR MASCOTA ---");
        String nombre = entrada.obligatorio("Nombre de mascota");
        String especie = entrada.especie();
        String raza = entrada.obligatorio("Raza");
        String sexo = entrada.obligatorio("Sexo");
        LocalDate nacimiento = entrada.nacimiento();
        double peso = entrada.positivo("Peso en kg");
        System.out.println(gestor.modificarMascota(id, nombre, especie, raza, sexo, nacimiento, peso));
    }

    private void consultarMascotas() {
        String id = entrada.idPropietario();
        if (id != null) System.out.println(gestor.listarMascotas(id));
    }

    private void consultarMascota() {
        String id = entrada.idMascota();
        if (id != null) System.out.println(gestor.detalleMascota(id));
    }
}
