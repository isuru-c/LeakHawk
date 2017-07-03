import sensors.PastbinSensor;

/**
 * Created by Isuru Chandima on 6/18/17.
 */
public class LeakHawk {

    public static void main(String[] args) {

        PastbinSensor pastbinSensor = new PastbinSensor();
        pastbinSensor.start();

    }

}
