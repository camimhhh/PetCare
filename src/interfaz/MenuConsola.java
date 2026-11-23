package interfaz;

import java.util.Scanner;
import negocio.GestorPerfiles;
import negocio.GestorPlanes;
import negocio.GestorSaludAgenda;

// Solo muestra el menú principal y envía cada opción al módulo correspondiente.
public class MenuConsola {
    private EntradaConsola entrada;
    private MenuPerfiles menuPerfiles;
    private MenuPlanes menuPlanes;
    private MenuSaludAgenda menuSalud;

    public MenuConsola(GestorPerfiles perfiles, GestorPlanes planes, GestorSaludAgenda salud) {
        Scanner teclado = new Scanner(System.in);
        entrada = new EntradaConsola(teclado, perfiles);
        menuPerfiles = new MenuPerfiles(entrada, perfiles);
        menuPlanes = new MenuPlanes(entrada, perfiles, planes);
        menuSalud = new MenuSaludAgenda(entrada, perfiles, salud);
    }

    public void iniciar() {
        String opcion = "";
        while (!opcion.equals("0")) {
            System.out.println("\n---- PETCARE ----");
            System.out.println("\n--- PERFILES ---");
            System.out.println("1. Registrar propietario");
            System.out.println("2. Consultar propietarios");
            System.out.println("3. Modificar propietario");
            System.out.println("4. Registrar mascota");
            System.out.println("5. Consultar mascotas de un propietario");
            System.out.println("6. Buscar mascota por ID");
            System.out.println("7. Modificar mascota");
            System.out.println("\n--- ALIMENTACIÓN Y ACTIVIDAD FÍSICA ---");
            System.out.println("8. Registrar plan de alimentación");
            System.out.println("9. Consultar historial de alimentación");
            System.out.println("10. Cerrar plan de alimentación");
            System.out.println("11. Registrar plan de actividad física");
            System.out.println("12. Consultar historial y minutos semanales");
            System.out.println("13. Cerrar plan de actividad física");
            System.out.println("\n--- SALUD VETERINARIA Y AGENDA ---");
            System.out.println("14. Abrir salud veterinaria y agenda");
            System.out.println("0. Salir");
            opcion = entrada.pedir("\nOpción: ").trim();

            switch (opcion) {
                case "1": case "2": case "3": case "4": case "5": case "6": case "7":
                    menuPerfiles.ejecutar(opcion); break;
                case "8": case "9": case "10": case "11": case "12": case "13":
                    menuPlanes.ejecutar(opcion); break;
                case "14": menuSalud.abrir(); break;
                case "0": System.out.println("Hasta pronto."); break;
                default: entrada.error("opción incorrecta.");
            }
        }
    }
}
