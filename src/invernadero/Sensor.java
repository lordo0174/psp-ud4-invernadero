package invernadero;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Sensor genérico del invernadero. Implementa Runnable (no hereda de Thread),
 * así que el mismo código puede ejecutarse en el hilo main o en un hilo propio.
 */
public abstract class Sensor implements Runnable {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final String nombre;
    private final String unidad;
    private final long periodo;       // ms entre lecturas
    private final int numLecturas;
    private final double umbralMin;
    private final double umbralMax;

    // Estadísticas propias de cada sensor
    private int lecturas;
    private double suma;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;
    private int alertas;

    protected Sensor(String nombre, String unidad, long periodo, int numLecturas,
                     double umbralMin, double umbralMax) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.periodo = periodo;
        this.numLecturas = numLecturas;
        this.umbralMin = umbralMin;
        this.umbralMax = umbralMax;
    }

    /** Devuelve una lectura del sensor. */
    public abstract double medir();

    @Override
    public void run() {
        for (int i = 0; i < numLecturas; i++) {
            double valor = medir();
            boolean alerta = valor < umbralMin || valor > umbralMax;
            registrar(valor, alerta);

            // Como Sensor no es un Thread, no tiene getName(): preguntamos al hilo actual
            String hilo = Thread.currentThread().getName();
            String linea = String.format("[%s] [%s] %s: %.2f %s",
                    LocalTime.now().format(HORA), hilo, nombre, valor, unidad);
            if (alerta) {
                linea += "  ⚠ ALERTA";
            }
            System.out.println(linea);

            try {
                Thread.sleep(periodo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void registrar(double valor, boolean alerta) {
        lecturas++;
        suma += valor;
        min = Math.min(min, valor);
        max = Math.max(max, valor);
        if (alerta) {
            alertas++;
        }
    }

    /** Resumen de las estadísticas del sensor en una línea. */
    public String informe() {
        if (lecturas == 0) {
            return String.format("%-12s | sin lecturas", nombre);
        }
        return String.format("%-12s | lecturas: %d | media: %.2f %s | mín: %.2f %s | máx: %.2f %s | alertas: %d",
                nombre, lecturas, suma / lecturas, unidad, min, unidad, max, unidad, alertas);
    }

    public String getNombre() {
        return nombre;
    }
}
