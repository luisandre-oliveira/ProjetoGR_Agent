package MIB;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Objects;

public class Actuator implements MibEntry {
    private final MibObj id;
    private final MibObj type;
    private MibObj status; // percentage between minValue and maxValue
    private final MibObj minValue;
    private final MibObj maxValue;
    private MibObj lastControlTime;

    public Actuator(String id, String type, int status, int minValue, int maxValue, LocalDateTime lastControlTime) {
        this.id = new MibObj("String", 0, "Tag identifying the actuator", "3.1", id);
        this.type = new MibObj("String", 0, "Text description for the type of actuator", "3.2", type);
        this.status = new MibObj("Integer", 1, "Configuration value set for the actuator", "3.3", String.valueOf(status));
        this.minValue = new MibObj("Integer", 0, "Minimum value possible for the configuration of the actuator", "3.4", String.valueOf(minValue));
        this.maxValue = new MibObj("Integer", 0, "Maximum value possible for the configuration of the actuator", "3.5", String.valueOf(maxValue));
        this.lastControlTime = new MibObj("Timestamp", 0, "Date and time when the last configuration/control operation was executed", "3.6", String.valueOf(lastControlTime));
    }

    // Getters
    public MibObj getId() { return id; }

    public MibObj getType() { return type; }

    public MibObj getStatus() { return status; }

    public MibObj getMinValue() { return minValue; }

    public MibObj getMaxValue() { return maxValue; }

    public MibObj getLastControlTime() { return lastControlTime; }

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

    public void setValue(int structure, int object) {
        // Get all declared fields of this class
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == MibObj.class) { // Check if it is a MibObj class object
                try {
                    field.setAccessible(true);
                    MibObj mibObj = (MibObj) field.get(this); // Get the specific object of each field ex: Id, Type, etc...

                    if (Objects.equals(mibObj.getIID(), structure + "." + object)) { // Check if the object IID equals the IID we're looking for
                        // TODO
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("Unable to access field: " + field.getName());
                }
            }
        }
    }
}
