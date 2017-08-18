import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import sensors.PastebinSensor;

/**
 * Created by Isuru Chandima on 6/18/17.
 */
public class LeakHawk {

    public static void main(String[] args) {

        PastebinSensor pastebinSensor = new PastebinSensor();
        pastebinSensor.start();

        final String TOPOLOGY_NAME = "LeakHawk-topology";

        Config config = new Config();
        config.setMessageTimeoutSecs(120);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("pastebin-spout", new PastebinSpout(), 2);
        builder.setBolt("post-download", new PostDownloadBolt() , 4).shuffleGrouping("pastebin-spout");
        builder.setBolt("pre-filter", new PreFilterBolt() , 3).shuffleGrouping("post-download");
        builder.setBolt("context-filter", new ContextFilterBolt() , 2).shuffleGrouping("pre-filter");
        builder.setBolt("evidence-classifier", new EvidenceClassifierBolt() , 1).shuffleGrouping("context-filter");
        builder.setBolt("content-classifier", new ContentClassifierBolt() , 1).shuffleGrouping("evidence-classifier");
        builder.setBolt("synthesizer", new Synthesizer(), 1).shuffleGrouping("content-classifier");

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
