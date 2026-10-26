package invernadero;

public class Fase3_Experimentos {

    public static void main(String[] args) throws InterruptedException {
        experimentoA();
        experimentoB();
        experimentoC();
    }

    /** A: imprimir el informe sin esperar al hilo, y después de esperarlo. */
    private static void experimentoA() throws InterruptedException {
        titulo("A · informe() sin esperar al hilo");

        Sensor temperatura = new Temperatura("Temperatura", 300, 3, 15, 30);
        Thread hilo = new Thread(temperatura, "sensor-Temperatura");
        hilo.start();

        System.out.println("Sin esperar  -> " + temperatura.informe());

        hilo.join();
        System.out.println("Tras join()  -> " + temperatura.informe());
    }

    /** B: llamar a run() en vez de start(). */
    private static void experimentoB() {
        titulo("B · riego.run() en vez de riego.start()");

        ProgramadorRiego riego = new ProgramadorRiego(3);

        long inicio = System.currentTimeMillis();
        riego.run();
        long fin = System.currentTimeMillis();

        System.out.println("Tiempo: " + (fin - inicio) + " ms");
        System.out.println("riego.getName()  = " + riego.getName());
        System.out.println("riego.getState() = " + riego.getState());
        System.out.println(riego.informe());
    }

    /** C: llamar dos veces a start() sobre el mismo hilo. */
    private static void experimentoC() throws InterruptedException {
        titulo("C · start() dos veces sobre el mismo objeto");

        ProgramadorRiego riego = new ProgramadorRiego(3);
        riego.start();
        riego.join();
        System.out.println("Estado tras join(): " + riego.getState());

        try {
            riego.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("Excepción capturada: " + e);
        }
        System.out.println("Estado final: " + riego.getState());
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("===== EXPERIMENTO " + texto + " =====");
    }
}
