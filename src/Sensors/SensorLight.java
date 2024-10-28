package Sensors;

import java.util.Random;

public class SensorLight {
    private static final int MAX = 1000;
    private static final int MIN = 0;
    private final Random random;
    private double lightValue;

    public SensorLight() {
        random = new Random();
        // Initializes with a value between 0 new(pitch black) and 1000 (really bright) in Lux
        this.lightValue = generateInitialLightValue();
    }

    private double generateInitialLightValue() {
        // Generates a value between 0 and 1000 Lux
        return MIN + random.nextDouble() * 900;
    }

    public double readLightValue() {
        // Simulates fluctuations in light intensity
        this.lightValue += random.nextGaussian();
        return Math.max(MIN, Math.min(MAX, this.lightValue));
    }

    // public static int getValue() { return random.nextInt(MAX + 1 - MIN) + MIN;}

     /* public static void main(String[] args) throws InterruptedException {
        SensorLight sensor = new SensorLight();

        System.out.println("Reading from the light sensor...");

        for(int i = 0; i < 10; i++ ) {
            double valueRead = sensor.readLightValue();
            System.out.println("Reading " + (i + 1) + ": " + valueRead + " Lux");
            Thread.sleep(500);
        }
    }
    */
}
