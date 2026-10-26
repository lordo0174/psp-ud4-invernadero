package invernadero;

public class Fase4_Vigilante {

    public static void main(String[] args) throws InterruptedException {
        Sensor temperatura = new Temperatura("Temperatura", 500, 5, 15, 30);
        Sensor humedad = new Humedad("Humedad", 600, 5, 40, 80);
        Sensor luz = new Luz("Luz", 400, 5, 200, 1500);
        ProgramadorRiego riego = new ProgramadorRiego();

        Thread hTemperatura = new Thread(temperatura, "sensor-" + temperatura.getNombre());
        Thread hHumedad = new Thread(humedad, "sensor-" + humedad.getNombre());
        Thread hLuz = new Thread(luz, "sensor-" + luz.getNombre());
        Thread[] invernadero = {hTemperatura, hHumedad, hLuz, riego};

        Thread vigilante = new Thread(new Vigilante(invernadero), "vigilante");
        // Daemon: la JVM no le espera. Hay que activarlo ANTES de start()
        vigilante.setDaemon(true);

        System.out.println("Antes de start():  " + Vigilante.estados(invernadero));

        vigilante.start();
        hTemperatura.start();
        hHumedad.start();
        hLuz.start();
        riego.start();

        // main espera a los cuatro del invernadero, pero NO al vigilante
        hTemperatura.join();
        hHumedad.join();
        hLuz.join();
        riego.join();

        System.out.println("Después de join(): " + Vigilante.estados(invernadero));
        System.out.println("vigilante: " + vigilante.getState() + ", daemon = " + vigilante.isDaemon());
        System.out.println("main termina y la JVM se cierra sin esperar al vigilante");
    }
}
