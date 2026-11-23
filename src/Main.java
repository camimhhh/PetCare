import interfaz.MenuConsola;
import negocio.DatosDemostracion;
import negocio.GestorPerfiles;
import negocio.GestorPlanes;
import negocio.GestorSaludAgenda;

public class Main {
    public static void main(String[] args) {
        GestorPerfiles perfiles = new GestorPerfiles();
        GestorPlanes planes = new GestorPlanes(perfiles);
        GestorSaludAgenda salud = new GestorSaludAgenda();


        DatosDemostracion.cargar(perfiles, planes, salud);

        MenuConsola menu = new MenuConsola(perfiles, planes, salud);
        menu.iniciar();
        }
    }
