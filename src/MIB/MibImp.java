package MIB;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class MibImp {
    private final Map<String,MibEntry> MIB; // String is for the MAC Address of the Sensor or Actuator
    private static MibImp instance; // Singleton design pattern implementation

    public Map<String,MibEntry> getMIB() {
        return MIB;
    }

    private static int getNumberOfAttributesInClass(int structure) {
        // Choose the class to inspect based on structure value
        Class<?> classToCheck = switch (structure) {
            case 1 -> Device.class;
            case 2 -> Sensor.class;
            case 3 -> Actuator.class;

            default -> throw new IllegalStateException("Unexpected value: " + structure);
        };

        // Count MibObj fields in the chosen class
        int count = 0;
        for (Field field : classToCheck.getDeclaredFields()) {
            if (field.getType() == MibObj.class) {
                count++;
            }
        }
        return count;
    }

    private static int getNumberOfInstances(int structure) {
        // Choose the class to inspect based on structure value
        Class<?> classToCheck = switch (structure) {
            case 1 -> Device.class;
            case 2 -> Sensor.class;
            case 3 -> Actuator.class;

            default -> throw new IllegalStateException("Unexpected value: " + structure);
        };

        int count = 0;
        for (MibEntry entry : instance.MIB.values()) {
            if(classToCheck.isInstance(entry)) {
                count++;
            }
        }

        return count;
    }

    private static String getInstanceOfObject(int structure, int object, int index1) {
        // Choose the class to inspect based on structure value
        Class<?> classToCheck = switch (structure) {
            case 1 -> Device.class;
            case 2 -> Sensor.class;
            case 3 -> Actuator.class;

            default -> throw new IllegalStateException("Unexpected value: " + structure);
        };

        int count = 0;
        for (MibEntry entry : instance.MIB.values()) {
            if(classToCheck.isInstance(entry)) {
                if(count == index1) {
                    return entry.getValue(structure,object);
                }
                count++;
            }
        }

        return null;
    }

    public static void setInstanceOfObject(String iid, String value) {
        String[] iidParts = iid.split("\\.");

        // Parse each part, checking if it exists, if not assign it to -1
        int structure = (iidParts.length > 0) ? Integer.parseInt(iidParts[0]) : -1;
        int object = (iidParts.length > 1) ? Integer.parseInt(iidParts[1]) : -1;
        int index1 = (iidParts.length > 2) ? Integer.parseInt(iidParts[2]) -1 : -1;

        // Choose the class to inspect based on structure value
        Class<?> classToCheck = switch (structure) {
            case 1 -> Device.class;
            case 2 -> Sensor.class;
            case 3 -> Actuator.class;
            default -> throw new IllegalStateException("Unexpected value: " + structure);
        };

        int count = 0;

        // Loop through the MIB entries
        for (MibEntry entry : instance.MIB.values()) {
            // Check if the entry is of the correct type
            if(classToCheck.isInstance(entry)) {
                // Check if the current index matches the desired index
                if(count == index1) {

                    String entryId = entry.getId().getValue();

                    switch (structure) {

                        case 1 : // In case the value I'm setting if of a Device
                            Device oldDevice = (Device) entry;

                            Device newDevice = switch (object) {

                                case 3 -> new Device(
                                        oldDevice.getId().getValue(),
                                        oldDevice.getType().getValue(),
                                        Integer.parseInt(value), // change to beaconRate value
                                        Integer.parseInt(oldDevice.getNSensors().getValue()),
                                        Integer.parseInt(oldDevice.getNActuators().getValue()),
                                        LocalDateTime.parse(oldDevice.getDateAndTime().getValue()),
                                        LocalDateTime.parse(oldDevice.getUpTime().getValue()),
                                        LocalDateTime.parse(oldDevice.getLastTimeUpdated().getValue()),
                                        Integer.parseInt(oldDevice.getOperationalStatus().getValue()),
                                        Integer.parseInt(oldDevice.getReset().getValue()));

                                case 6 -> new Device(
                                        oldDevice.getId().getValue(),
                                        oldDevice.getType().getValue(),
                                        Integer.parseInt(oldDevice.getBeaconRate().getValue()),
                                        Integer.parseInt(oldDevice.getNSensors().getValue()),
                                        Integer.parseInt(oldDevice.getNActuators().getValue()),
                                        LocalDateTime.parse(value), // change to dateAndTime value
                                        LocalDateTime.parse(oldDevice.getUpTime().getValue()),
                                        LocalDateTime.parse(oldDevice.getLastTimeUpdated().getValue()),
                                        Integer.parseInt(oldDevice.getOperationalStatus().getValue()),
                                        Integer.parseInt(oldDevice.getReset().getValue()));

                                case 10 -> new Device(
                                        oldDevice.getId().getValue(),
                                        oldDevice.getType().getValue(),
                                        Integer.parseInt(oldDevice.getBeaconRate().getValue()),
                                        Integer.parseInt(oldDevice.getNSensors().getValue()),
                                        Integer.parseInt(oldDevice.getNActuators().getValue()),
                                        LocalDateTime.parse(oldDevice.getDateAndTime().getValue()),
                                        LocalDateTime.parse(oldDevice.getUpTime().getValue()),
                                        LocalDateTime.parse(oldDevice.getLastTimeUpdated().getValue()),
                                        Integer.parseInt(oldDevice.getOperationalStatus().getValue()),
                                        Integer.parseInt(value)); // change to reset value

                                default -> throw new IllegalStateException("Unexpected value: " + object);
                            };

                            instance.MIB.put(entryId, newDevice);
                            break;

                        case 3 : // In case the value I'm setting if of an Actuator
                            Actuator oldActuator = (Actuator) entry;

                            Actuator newActuator = new Actuator(
                                    oldActuator.getId().getValue(),
                                    oldActuator.getType().getValue(),
                                    Integer.parseInt(value),
                                    Integer.parseInt(oldActuator.getMinValue().getValue()),
                                    Integer.parseInt(oldActuator.getMaxValue().getValue()),
                                    LocalDateTime.now());

                            instance.MIB.put(entryId, newActuator);
                            break;
                    }
                }
                count++;
            }
        }
    }

    public static String getAllValues(int structure, int object) {
        StringBuilder toSend = new StringBuilder();

        for(int N = 0; N < getNumberOfInstances(structure); N++) {
            toSend.append(getInstanceOfObject(structure, object, N));
            toSend.append(",");
        }

        if(!toSend.isEmpty()) {
            toSend.deleteCharAt(toSend.length() - 1);
        }

        return toSend.toString();
    }

    public static String getAllValuesInRange(int structure, int object, int index1, int index2) {
        StringBuilder toSend = new StringBuilder();

        int numberIndexsToBeSearched = index2 - index1 + 1; // ex: 3.1.1.2 -> 2 - 1 + 1 = 2 (obv there's only two in this case and project)

        for(int N = 0; N < numberIndexsToBeSearched; N++) {
            toSend.append(getInstanceOfObject(structure,object,N));
            toSend.append(",");
        }

        if(!toSend.isEmpty()) {
            toSend.deleteCharAt(toSend.length() - 1);
        }

        return toSend.toString();
    }

    public static MibImp getInstance() {
        if (instance == null) {
            instance = new MibImp();  // Initialize if not yet created
        }
        return instance;
    }

    public static synchronized String findValueByIID(String iid) {
        String[] iidParts = iid.split("\\.");

        // Parse each part, checking if it exists, if not assign it to -1
        int structure = (iidParts.length > 0) ? Integer.parseInt(iidParts[0]) : -1;
        int object = (iidParts.length > 1) ? Integer.parseInt(iidParts[1]) : -1;
        int index1 = (iidParts.length > 2) ? Integer.parseInt(iidParts[2]) : -1;
        int index2 = (iidParts.length > 3) ? Integer.parseInt(iidParts[3]) : -1;

        if(structure > 0 && object == 0 && index1 == -1 && index2 == -1) { // GET p.e. 3.0
            return String.valueOf(getNumberOfAttributesInClass(structure));

        } else if (structure > 0 && object > 0 && index1 == -1 && index2 == -1) { // GET p.e 3.1 -> 3.1.1
            return getInstanceOfObject(structure,object,0);

        } else if (structure > 0 && object > 0 && index1 == 0 && index2 == -1) { // GET p.e. 3.1.0
            return String.valueOf(getNumberOfInstances(structure));

        } else if (structure > 0 && object > 0 && index1 > 0 && index2 == -1) { // GET p.e. 3.1.1
            // take 1 from index1 because it is stored in a [0,1] not as a [1,2]
            return getInstanceOfObject(structure,object,index1 - 1);

        } else if (structure > 0 && object > 0 && index1 == 0 && index2 == 0) { // GET p.e. 3.1.0.0
            return getAllValues(structure, object);


        } else if (structure > 0 && object > 0 && index1 != 0 && index2 > index1) { // GET 3.1.1.2
            return getAllValuesInRange(structure,object,index1,index2);

        } else if (structure == -1 || object == -1) { // BOTH OF THEM ARE ERRORS THAT NEED TO BE HANDLED
            // Error handling is done when it returns to the function
            // that calls this one, since it will return '-1',
            // so a 5 will be sent to the ErrorList
            // (check CommUDP.java for the G case inside the switch)
            System.out.println("\n-- ERROR: INVALID IID. --");
        }

        return String.valueOf(-1);
    }

    private MibImp() {
        this.MIB = new TreeMap<>();
        MIBLazyLoader();
    }

    private void MIBLazyLoader() {
        LocalDateTime initTimeDS = LocalDateTime.now().minusMinutes(5); // init time for device and sensors
        LocalDateTime initTimeA = LocalDateTime.now().minusMinutes(3); // init time for actuators
        long timeDiff = Duration.between(initTimeDS,LocalDateTime.now()).getSeconds();

        Device device = new Device("FF:FF:FF:FF:FF:FF","Lights & A/C Conditioning",10,2,2,initTimeDS,calculateUpTime(timeDiff),initTimeDS,1,0); // beaconRate = 10 seconds
        Sensor sensorLight = new Sensor("00:11:22:33:44:55","Light",25,0,800,initTimeDS); // 200 LUMENS
        Sensor sensorTemp = new Sensor("99:88:77:66:55:44","Temperature",80,-10,40,initTimeDS); // 10 ºC
        Actuator actuatorLight = new Actuator("AA:BB:CC:DD:EE:FF","Light",75,0,800,initTimeA); // 600 LUMENS
        Actuator actuatorTemp = new Actuator("FF:EE:DD:CC:BB:AA","Temperature",60,-10,40,initTimeA); // 20 ºC

        MIB.put(device.getId().getValue(),device);
        MIB.put(sensorLight.getId().getValue(),sensorLight);
        MIB.put(sensorTemp.getId().getValue(),sensorTemp);
        MIB.put(actuatorLight.getId().getValue(),actuatorLight);
        MIB.put(actuatorTemp.getId().getValue(),actuatorTemp);
    }

    private LocalDateTime calculateUpTime(long time) {
        long days = time / (24 * 3600);
        time %= 24 * 3600;

        long hours = time / 3600;
        time %= 3600;

        long minutes = time /60;
        long seconds = time % 60;

        return LocalDateTime.of(0,1,1,0,0)
                .plusDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
    }

}
