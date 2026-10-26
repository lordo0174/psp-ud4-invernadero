package invernadero;

public class Fase1_Secuencial {

    public static void main(String[] args) {
        Sensor temperatura = new Temperatura("Temperatura", 500, 5, 15, 30);
        Sensor humedad = new Humedad("Humedad", 600, 5, 40, 80);
        Sensor luz = new Luz("Luz", 400, 5, 200, 1500);
        ProgramadorRiego riego = new ProgramadorRiego();

        long inicio = System.currentTimeMillis();

        temperatura.run();
        humedad.run();
        luz.run();
        riego.run();

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
