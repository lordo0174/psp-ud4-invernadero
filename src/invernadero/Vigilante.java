package invernadero;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

/**
 * Muestra cada 300 ms el estado de los hilos que vigila. Tiene un bucle infinito,
 * así que hay que lanzarlo como daemon para que no impida que el programa termine.
 */
public class Vigilante implements Runnable {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final long PERIODO = 300;   // ms entre comprobaciones

    private final Thread[] hilos;

    public Vigilante(Thread... hilos) {
        this.hilos = hilos;
    }

    @Override
    public void run() {
        while (true) {
            System.out.printf("[%s] [%s] %s%n",
                    LocalTime.now().format(HORA), Thread.currentThread().getName(), estados(hilos));
            try {
                Thread.sleep(PERIODO);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /** Devuelve en una línea el nombre y el estado (getState()) de cada hilo. */
    public static String estados(Thread... hilos) {
        StringJoiner linea = new StringJoiner(" | ");
        for (Thread hilo : hilos) {
            linea.add(hilo.getName() + "=" + hilo.getState());
        }
        return linea.toString();
    }
}
