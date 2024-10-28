package Agent;


import Sensors.SensorLight;
import java.text.DecimalFormat;

public class AgentL_SNMP {

    final static DecimalFormat format = new DecimalFormat("#.##");

    public static void main (String[] args) throws InterruptedException {
        getLightValue();
    }

    public static void setTargetLightValue(double targetLight) throws InterruptedException {
        SensorLight sensor = new SensorLight();

        System.out.println("Actuator = OFF");
        double lightValue = getLightValue();

        if(targetLight != lightValue) {

        }
    }

    public static double getLightValue() throws InterruptedException {
        SensorLight sensor = new SensorLight();
        double sumForAvg = 0, lightValue = 0;

        System.out.println("Reading from the light sensor...");

        for(int i = 0; i < 5; i++ ) {
            double valueRead = sensor.readLightValue();
            sumForAvg += valueRead;

            Thread.sleep(10);
        }

        lightValue = sumForAvg / 5;
        System.out.println("Light Measured: " + format.format(lightValue) + " Lumens");
        return lightValue;
    }
}
