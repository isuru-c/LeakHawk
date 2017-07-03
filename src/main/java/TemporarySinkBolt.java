import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class TemporarySinkBolt extends BaseRichBolt {

    OutputCollector collector;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    public void execute(Tuple tuple) {

        String post_url = tuple.getString(0);
        String key = tuple.getString(1);
        String date = tuple.getString(2);
        String title = tuple.getString(3);
        String user = tuple.getString(4);

        System.out.println("\nUrl: " + post_url + "\nKey: " + key + "\nDate: " + date + "\nTitle: " + title + "\nUser: " + user);

        collector.ack(tuple);

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("temp"));
    }
}
