package invernadero;

import java.util.concurrent.ThreadLocalRandom;

public class Luz extends Sensor {

    public Luz(String nombre, long periodo, int numLecturas, double umbralMin, double umbralMax) {
        super(nombre, "lux", periodo, numLecturas, umbralMin, umbralMax);
    }

    @Override
    public double medir() {
        return ThreadLocalRandom.current().nextDouble(0, 2000);
    }
}
