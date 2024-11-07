package MIB;

public interface MibEntry {
    String getValue(int structure, int object); // Common method to retrieve the MAC Address for both Sensors and Actuators
}
