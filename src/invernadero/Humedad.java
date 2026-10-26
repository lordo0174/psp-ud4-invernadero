package invernadero;

import java.util.concurrent.ThreadLocalRandom;

public class Humedad extends Sensor {

    public Humedad(String nombre, long periodo, int numLecturas, double umbralMin, double umbralMax) {
        super(nombre, "%", periodo, numLecturas, umbralMin, umbralMax);
    }

    @Override
    public double medir() {
        return ThreadLocalRandom.current().nextDouble(20, 100);
    }
}
