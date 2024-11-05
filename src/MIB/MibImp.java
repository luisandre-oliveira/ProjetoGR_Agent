package MIB;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MibImp {
    private static Map<String,MibEntry> MIB; // String is for the MAC Address of the Sensor or Actuator
    private static MibImp instance; // Singleton design pattern implementation

    private MibImp() {
        this.MIB = new HashMap<>();
        MIBLazyLoader();
    }

    private void MIBLazyLoader() {
        Sensor sensorLight = new Sensor("00:1A:2B:3C:4D:5E","Light",100,0,800,LocalDateTime.now().minusMinutes(5));
        Actuator actuatorLight = new Actuator("AA:BB:CC:DD:EE:FF","Light",600,0,100,LocalDateTime.now().minusMinutes(3));

        MIB.put(sensorLight.getId(),sensorLight);
        MIB.put(actuatorLight.getId(), actuatorLight);
    }

    public static MibImp getInstance() {
        if (instance == null) {
            instance = new MibImp();  // Initialize if not yet created
        }
        return instance;    }

    public static synchronized MibEntry findIID(String iid) {
        System.out.println("INICIALMENTE: " + iid);
        String[] iidParts = iid.split("\\.");

        // Parse each part, checking if it exists, if not assign it to -1
        int structure = (iidParts.length > 0) ? Integer.parseInt(iidParts[0]) : -1;
        int object = (iidParts.length > 1) ? Integer.parseInt(iidParts[1]) : -1;
        int index1 = (iidParts.length > 2) ? Integer.parseInt(iidParts[2]) : -1;
        int index2 = (iidParts.length > 3) ? Integer.parseInt(iidParts[3]) : -1;

        System.out.println("PARTES: " + structure + "." + object + "." + index1 + "." + index2);

        for(MibEntry entry : MIB.values()) {
            System.out.println(entry.getIID());

            if(structure > 0 && object == 0 && index1 == -1 && index2 == -1) { // GET p.e. 3.0
                System.out.println("A" + structure + "." + object + "." + index1 + "." + index2);
            } else if (structure > 0 && object > 0 && index1 == -1 && index2 == -1) { // GET p.e 3.1
                System.out.println("B" + structure + "." + object + "." + index1 + "." + index2);
            } else if (structure > 0 && object > 0 && index1 == 0 && index2 == -1) { // GET p.e. 3.1.0
                System.out.println("C" + structure + "." + object + "." + index1 + "." + index2);
            } else if (structure > 0 && object > 0 && index1 == 0 && index2 > index1) {
                System.out.println("D" + structure + "." + object + "." + index1 + "." + index2);
            }
            // TODO MORE EDGE CASES + WHAT IS SUPPOSED TO HAPPEN
            if(Objects.equals(entry.getIID(), structure + "." + object)) {
                return entry;
            }
        }
        return null;
    }

    /*public synchronized update(String s) {

    }*/

    /* private int generateInitialLightValue() {
        // Generates a value between 0 and 800 Lux
        return (int) (this.getMinValue() + random.nextDouble() * 900);
    }*/
}
