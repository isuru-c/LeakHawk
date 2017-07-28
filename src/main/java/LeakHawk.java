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
        builder.setSpout("pastbin-in", new PastebinSpout(), 2);
        builder.setBolt("process-1", new PostDownloadBolt() , 4).shuffleGrouping("pastbin-in");
        builder.setBolt("process-2", new PreFilterBolt() , 1).shuffleGrouping("process-1");
        builder.setBolt("process-3", new ContextFilterBolt() , 1).shuffleGrouping("process-2");
        builder.setBolt("process-4", new EvidenceClassifierBolt() , 1).shuffleGrouping("process-3");
        builder.setBolt("process-5", new ContentClassifierBolt() , 1).shuffleGrouping("process-4");
        builder.setBolt("terminator", new EndOfClassifierBolt(), 1).shuffleGrouping("process-5");

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
