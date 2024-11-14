package MIB;

public interface MibEntry {
    String getValue(int structure, int object); // Method to get the value in a certain IID
    MibObj getId(); // Method to retrieve the MAC Address for both Sensors and Actuators
}
