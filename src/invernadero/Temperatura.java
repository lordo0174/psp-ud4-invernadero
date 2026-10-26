package invernadero;

import java.util.concurrent.ThreadLocalRandom;

public class Temperatura extends Sensor {

    public Temperatura(String nombre, long periodo, int numLecturas, double umbralMin, double umbralMax) {
        super(nombre, "°C", periodo, numLecturas, umbralMin, umbralMax);
    }

    @Override
    public double medir() {
        return ThreadLocalRandom.current().nextDouble(10, 40);
    }
}
