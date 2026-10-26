package invernadero;

public class Fase2_Concurrente {

    public static void main(String[] args) throws InterruptedException {
        Sensor temperatura = new Temperatura("Temperatura", 500, 5, 15, 30);
        Sensor humedad = new Humedad("Humedad", 600, 5, 40, 80);
        Sensor luz = new Luz("Luz", 400, 5, 200, 1500);
        ProgramadorRiego riego = new ProgramadorRiego();

        // Sensor es Runnable: hay que meterlo en un Thread y darle nombre al crearlo
        Thread hTemperatura = new Thread(temperatura, "sensor-" + temperatura.getNombre());
        Thread hHumedad = new Thread(humedad, "sensor-" + humedad.getNombre());
        Thread hLuz = new Thread(luz, "sensor-" + luz.getNombre());

        long inicio = System.currentTimeMillis();

        hTemperatura.start();
        hHumedad.start();
        hLuz.start();
        riego.start();

        // main espera a que terminen los cuatro antes de seguir
        hTemperatura.join();
        hHumedad.join();
        hLuz.join();
        riego.join();

        long fin = System.currentTimeMillis();

        System.out.println();
        System.out.println("===== INFORME =====");
        System.out.println(temperatura.informe());
        System.out.println(humedad.informe());
        System.out.println(luz.informe());
        System.out.println(riego.informe());
        System.out.printf("Tiempo total: %d ms (%.2f s)%n", fin - inicio, (fin - inicio) / 1000.0);
    }
}
