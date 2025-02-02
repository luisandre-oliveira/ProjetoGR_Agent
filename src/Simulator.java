import MIB.Actuator;
import MIB.MibEntry;
import MIB.MibImp;
import MIB.Sensor;

import java.time.LocalDateTime;
import java.util.Objects;

public class Simulator implements Runnable {

    private static final int SLEEP_INTERVAL = 1;
    private final MibImp instance = MibImp.getInstance();

    @Override
    public synchronized void run() {

        while(true) {
            try {
                // Iterate through all entries in MIB
                for(MibEntry sensorEntry : instance.getMIB().values()) {
                    if(sensorEntry instanceof Sensor sensor) {

                        for(MibEntry actuatorEntry : instance.getMIB().values()) {
                            if(actuatorEntry instanceof Actuator actuator) {
                                if(Objects.equals(actuator.getType().getValue(), sensor.getType().getValue())) {
                                    // updateSensorValue(sensor,actuator);

                                    if(Objects.equals(sensor.getType().getValue(), "Temperature")) {
                                        updateTemperatureValue(sensor,actuator);
                                    } else if (Objects.equals(sensor.getType().getValue(), "Light")) {
                                        updateLightValue(sensor,actuator);
                                        // doesn't include a lightSimulation like temperature does later on
                                        // because I don't find it to be needed since light doesn't fluctuate
                                        // a lot in a short amount of time excluding when something gets in
                                        // front of the light source which I'm not accounting for that edge case.
                                    }
                                }
                            }
                        }
                    }
                }

                // Sleep for the specified interval
                Thread.sleep(SLEEP_INTERVAL * 1000);
            } catch (InterruptedException e) {
                System.out.println("\n-- ERROR: Simulator thread interrupted. --");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void updateLightValue(Sensor sensor, Actuator actuator) {
        String lightPercentageValue = sensor.getStatus().getValue();
        String actuatorPercentageValue = actuator.getStatus().getValue();

        Sensor updatedSensor = new Sensor(
                sensor.getId().getValue(),
                sensor.getType().getValue(),
                Integer.parseInt(actuatorPercentageValue),
                Integer.parseInt(sensor.getMinValue().getValue()),
                Integer.parseInt(sensor.getMaxValue().getValue()),
                LocalDateTime.now()
        );

        synchronized (instance) {
            instance.getMIB().put(updatedSensor.getId().getValue(), updatedSensor);
        }

        System.out.println("\n-- Updated Light Sensor --");
        System.out.println("Last Value = " + lightPercentageValue + " %\nNew Value = " + actuatorPercentageValue + " %\nTarget Value = " + actuatorPercentageValue + " %");
    }

    private void updateTemperatureValue(Sensor sensor, Actuator actuator) {
        String temperaturePercentageValue = sensor.getStatus().getValue(); // percentage
        String actuatorPercentageValue = actuator.getStatus().getValue(); // value to work toward
        String minValue = sensor.getMinValue().getValue(); // min value in Celsius
        String maxValue = sensor.getMaxValue().getValue(); // max value in Celsius

        int sensorRealValue = calculateValueFromRange(temperaturePercentageValue, minValue, maxValue);
        int actuatorRealValue = calculateValueFromRange(actuatorPercentageValue, minValue, maxValue);

        int calculatedValue = temperatureSimulation(sensorRealValue, actuatorRealValue); // updated value to be fed to the sensor

        int newValue = calculatePercentageFromRange(calculatedValue, minValue, maxValue);

        Sensor updatedSensor = new Sensor(
                sensor.getId().getValue(),
                sensor.getType().getValue(),
                newValue,
                Integer.parseInt(minValue),
                Integer.parseInt(maxValue),
                LocalDateTime.now()
        );

        synchronized (instance) {
            instance.getMIB().put(updatedSensor.getId().getValue(), updatedSensor);
        }

        System.out.println("\n-- Updated Temperature Sensor --");
        System.out.println("Last Value = " + temperaturePercentageValue + " %\nNew Value = " + newValue + " %\nTarget Value = " + actuatorPercentageValue + " %");
    }

    private int temperatureSimulation(int T_current, int T_target) {
        double k = 0.1; // cooling and warming rate
        double timeSteps = 1;

        return (int) (T_current + (k * (T_target - T_current) * timeSteps));

    }

    private int calculateValueFromRange(String percentage, String min, String max) {
        int PERCENTAGE = Integer.parseInt(percentage); int MIN = Integer.parseInt(min); int MAX = Integer.parseInt(max);
        int RANGE = MAX - MIN;
        return MIN + (RANGE * PERCENTAGE) / 100;
    }

    private int calculatePercentageFromRange(int VALUE, String min, String max) {
        int MIN = Integer.parseInt(min);
        int MAX = Integer.parseInt(max);
        int RANGE = MAX - MIN;

        if (RANGE == 0) {
            throw new IllegalArgumentException("Range cannot be zero.");
        }

        return (int) ((double)(VALUE - MIN) * 100 / RANGE);
    }

}
