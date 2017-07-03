import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import sensors.PastbinSensor;

/**
 * Created by Isuru Chandima on 6/18/17.
 */
public class LeakHawk {

    public static void main(String[] args) {

        PastbinSensor pastbinSensor = new PastbinSensor();
        pastbinSensor.start();

        final String TOPOLOGY_NAME = "storm-test-topology";

        Config config = new Config();
        config.setMessageTimeoutSecs(120);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("pastbin-in", new PastbinSpout(), 2);
        builder.setBolt("process-1", new TemporaryProcessBolt() , 3).shuffleGrouping("pastbin-in");
        builder.setBolt("terminator", new TemporarySinkBolt(), 1).shuffleGrouping("process-1");

        final LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cluster.killTopology(TOPOLOGY_NAME);
                cluster.shutdown();
            }
        });

    }

}
