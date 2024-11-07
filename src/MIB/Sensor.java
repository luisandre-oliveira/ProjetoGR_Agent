package MIB;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Objects;

public class Sensor implements MibEntry {
    private final MibObj id;
    private final MibObj type;
    private MibObj status; // percentage between minValue and maxValue
    private final MibObj minValue;
    private final MibObj maxValue;
    private MibObj lastSamplingTime;

    public Sensor(String id, String type, int status, int minValue, int maxValue, LocalDateTime lastSamplingTime) {
        this.id = new MibObj("String", 0, "Tag identifying the sensor", "2.1", id);
        this.type = new MibObj("String", 0, "Text description for the type of sensor", "2.2", type);
        this.status = new MibObj("Integer", 0, "Last value sampled in percent of range", "2.3", String.valueOf(status));
        this.minValue = new MibObj("Integer", 0, "Minimum value possible for the sampling values of the sensor", "2.4", String.valueOf(minValue));
        this.maxValue = new MibObj("Integer", 0, "Maximum value possible for the sampling values of the sensor", "2.5", String.valueOf(maxValue));
        this.lastSamplingTime = new MibObj("Timestamp", 0, "TTime elapsed since the last sample was obtained by the sensor", "2.6", String.valueOf(lastSamplingTime));
    }

    /* private int generateInitialLightValue() {
        // Generates a value between 0 and 800 Lux
        return (int) (this.getMinValue() + random.nextDouble() * 900);
    }*/

    /* public double readLightValue() {
        // Simulates fluctuations in light intensity
        this.lightValue += random.nextGaussian();
        return Math.max(this.getMinValue(), Math.min(this.getMaxValue(), this.getStatus()));
    }*/

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

    // Getters
    public MibObj getId() { return id; }

    public MibObj getType() { return type; }

    public MibObj getStatus() { return status; }

    public MibObj getMinValue() { return minValue; }

    public MibObj getMaxValue() { return maxValue; }

    public MibObj getLastSamplingTime() { return lastSamplingTime; }

    @Override
    public String getValue(int structure, int object) {
        // Get all declared fields of this class
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == MibObj.class) { // Check if it is a MibObj class object
                try {
                    field.setAccessible(true);
                    MibObj mibObj = (MibObj) field.get(this); // Get the specific object of each field ex: Id, Type, etc...

                    if (Objects.equals(mibObj.getIID(), structure + "." + object)) { // Check if the object IID equals the IID we're looking for
                        return mibObj.getValue();
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("Unable to access field: " + field.getName());
                }
            }
        }
        return null;
    }
}
