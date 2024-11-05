package MIB;

import java.time.LocalDateTime;

public class Actuator implements MibEntry{
    private MibObj id;
    private MibObj type;
    private MibObj status;
    private MibObj minValue;
    private MibObj maxValue;
    private MibObj lastControlTime;

    public Actuator(String id, String type, int status, int minValue, int maxValue, LocalDateTime lastControlTime) {
        this.id = new MibObj("String", 0, "Tag identifying the actuator","3.1",id);
        this.type = new MibObj("String", 0, "Text description for the type of actuator","3.2",type);
        this.status = new MibObj("Integer", 1, "Configuration value set for the actuator","3.3",String.valueOf(status));
        this.minValue = new MibObj("Integer", 0, "Minimum value possible for the configuration of the actuator","3.4",String.valueOf(minValue));
        this.maxValue = new MibObj("Integer", 0, "Maximum value possible for the configuration of the actuator","3.5",String.valueOf(maxValue));
        this.lastControlTime = new MibObj("Timestamp", 0, "Date and time when the last configuration/control operation was executed","3.6",String.valueOf(lastControlTime));
    }

    // Getters
    public String getId() {
        return id.getValue();
    }
    public MibObj getType() {
        return type;
    }
    public MibObj getStatus() {
        return status;
    }
    public MibObj getMinValue() {
        return minValue;
    }
    public MibObj getMaxValue() {
        return maxValue;
    }
    public MibObj getLastControlTime() {
        return lastControlTime;
    }

    @Override
    public String getValue() {
        return getStatus().getValue();
    }

    @Override
    public String getIID() { // TODO
        return null;
    }
}
