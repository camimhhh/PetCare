import interfaz.MenuConsola;
import negocio.GestorPerfiles;

public class Main {
    public static void main(String[] args) {
        GestorPerfiles gestor = new GestorPerfiles();
        MenuConsola menu = new MenuConsola(gestor);
        menu.iniciar();
    }
}
