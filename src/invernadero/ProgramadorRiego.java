package invernadero;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Programador del riego. Hereda de Thread, así que el nombre del hilo
 * se fija en el constructor con super("riego").
 */
public class ProgramadorRiego extends Thread {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final int CICLOS_POR_DEFECTO = 5;
    private static final long PAUSA = 700;               // ms entre ciclos
    private static final double LITROS_POR_RIEGO = 2.5;

    private final int numCiclos;
    private int ciclos;
    private double litros;

    public ProgramadorRiego() {
        this(CICLOS_POR_DEFECTO);
    }

    public ProgramadorRiego(int numCiclos) {
        super("riego");
        this.numCiclos = numCiclos;
    }

    @Override
    public void run() {
        for (int i = 1; i <= numCiclos; i++) {
            boolean abierta = i % 2 == 1;   // alterna: ABIERTA, cerrada, ABIERTA...
            ciclos++;
            if (abierta) {
                litros += LITROS_POR_RIEGO;
            }

            // currentThread() muestra el hilo que ejecuta de verdad (main si se llama a run() directamente)
            System.out.printf("[%s] [%s] Riego ciclo %d: válvula %s (total %.1f L)%n",
                    LocalTime.now().format(HORA), Thread.currentThread().getName(),
                    i, abierta ? "ABIERTA" : "cerrada", litros);

            try {
                Thread.sleep(PAUSA);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /** Resumen del riego en una línea. */
    public String informe() {
        return String.format("%-12s | ciclos: %d | litros: %.1f L", getName(), ciclos, litros);
    }
}
