package MIB;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Objects;

public class Device implements MibEntry {
    private MibObj id;
    private MibObj type;
    private MibObj beaconRate;
    private MibObj nSensors;
    private MibObj nActuators;
    private MibObj dateAndTime;
    private MibObj upTime;
    private MibObj lastTimeUpdated;
    private MibObj operationalStatus;
    private MibObj reset;

    public Device(String id, String type, int beaconRate, int nSensors, int nActuators, LocalDateTime dateAndTime,
                  LocalDateTime upTime, LocalDateTime lastTimeUpdated, int operationalStatus, int reset) {
        this.id = new MibObj("String", 0, "Tag identifying the device", "1.1", id);
        this.type = new MibObj("String", 0, "Text description for the type of the device", "1.2", type);
        this.beaconRate = new MibObj("Integer", 1, "Frequency rate in seconds for notifications", "1.3", String.valueOf(beaconRate));
        this.nSensors = new MibObj("Integer", 0, "Number of sensors implemented in the device", "1.4", String.valueOf(nSensors));
        this.nActuators = new MibObj("Integer", 0, "Number of actuators implemented in the device", "1.5", String.valueOf(nActuators));
        this.dateAndTime = new MibObj("Timestamp", 1, "System date and time setup in the device", "1.6", String.valueOf(dateAndTime));
        this.upTime = new MibObj("Timestamp", 0, "Time working since the last reboot", "1.7", String.valueOf(upTime));
        this.lastTimeUpdated = new MibObj("Timestamp", 0, "Date and time of the last update of any object in the device", "1.8", String.valueOf(lastTimeUpdated));
        this.operationalStatus = new MibObj("Integer", 0, "Operational state of the device", "1.9", String.valueOf(operationalStatus));
        this.reset = new MibObj("Integer", 1, "Value 0 means no reset and value 1 means a reset procedure", "1.10", String.valueOf(reset));
    }

    public MibObj getId() { return id; }

    public MibObj getType() { return type; }

    public MibObj getBeaconRate() { return beaconRate; }

    public MibObj getNSensors() { return nSensors; }

    public MibObj getNActuators() { return nActuators; }

    public MibObj getDateAndTime() { return dateAndTime; }

    public MibObj getUpTime() { return upTime; }

    public MibObj getLastTimeUpdated() { return lastTimeUpdated; }

    public MibObj getOperationalStatus() { return operationalStatus; }

    public MibObj getReset() { return reset; }

    @Override
    public String getIID() {
        return null;
    }

    @Override
    public String getValue(int structure, int object) {
        // Get all declared fields of this class
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == MibObj.class) {
                try {
                    field.setAccessible(true);
                    MibObj mibObj = (MibObj) field.get(this);

                    if (Objects.equals(mibObj.getIID(), structure + "." + object)) {
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
