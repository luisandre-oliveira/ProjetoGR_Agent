package MIB;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class MibImp {
    private Map<String,MibEntry> MIB; // String is for the MAC Address of the Sensor or Actuator
    private static MibImp instance; // Singleton design pattern implementation

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
            System.out.println(entry.getId().getValue());
            // Check if the entry is of the correct type
            if(classToCheck.isInstance(entry)) {
                // Check if the current index matches the desired index
                if(count == index1) {

                    String entryId = entry.getId().getValue();

                    switch (structure) {

                        case 1 :
                            Device oldDevice = (Device) entry;
                            break;

                        case 3 :
                            Actuator oldActuator = (Actuator) entry;
                            Actuator newActuator = new Actuator(oldActuator.getId().getValue(), oldActuator.getType().getValue(), Integer.parseInt(value),
                                    Integer.parseInt(oldActuator.getMinValue().getValue()), Integer.parseInt(oldActuator.getMaxValue().getValue()),
                                    LocalDateTime.parse(oldActuator.getLastControlTime().getValue()));

                            instance.MIB.put(entryId, newActuator);
                            break;

                    }

                    System.out.println("Updated Entry = " + instance.MIB.get(entryId).getId().getValue());
                }
                count++;
            }
        }
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
            // TODO GET VALUES OF ALL INSTANCES, MAKE A STRING WITH ALL VALUES, THEN PARSE (MAYBE???)
            System.out.println("E ->" + structure + "." + object + "." + index1 + "." + index2);

        } else if (structure > 0 && object > 0 && index1 == 0 && index2 > index1) { // GET 3.1.1.2
            // TODO GET ALL VALUES OF ALL INSTANCES IN THE RANGE OF INDEX_1 TO INDEX_2
            System.out.println("F ->" + structure + "." + object + "." + index1 + "." + index2);

        } else if (structure == -1 || object == -1) { // BOTH OF THEM ARE ERRORS THAT NEED TO BE HANDLED
            // TODO CHANGE ERROR LIST TO INCLUDE A 5 -> INVALID IID
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
        Sensor sensorTemp = new Sensor("99:88:77:66:55:44","Temperature",40,-10,40,initTimeDS); // 10 ºC
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

        LocalDateTime timeAsLocalDateTime = LocalDateTime.of(0,1,1,0,0)
                .plusDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);

        return timeAsLocalDateTime;
    }

    /*public synchronized update(String s) {

    }*/

    /* private int generateInitialLightValue() {
        // Generates a value between 0 and 800 Lux
        return (int) (this.getMinValue() + random.nextDouble() * 900);
    }*/


}
