package MIB;

public interface MibEntry {
    String getIID();
    String getValue(); // Common method to retrieve the MAC Address for both Sensors and Actuators
}
