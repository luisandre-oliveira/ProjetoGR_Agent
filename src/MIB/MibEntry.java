package MIB;

public interface MibEntry {
    String getIID();
    String getValue(int structure, int object); // Common method to retrieve the MAC Address for both Sensors and Actuators
}
