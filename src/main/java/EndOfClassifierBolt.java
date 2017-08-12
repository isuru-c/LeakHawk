import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by root on 7/28/17.
 */
public class EndOfClassifierBolt extends BaseRichBolt {

    OutputCollector collector;
    static int count = 0;
    private String sensitivity;
//    SensitivityPredictor sensitivityPredictor = new SensitivityPredictor();

    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    public void execute(Tuple tuple) {

        String type = tuple.getString(0);
        String key = tuple.getString(1);
        String date = tuple.getString(2);
        String user = tuple.getString(3);
        String title = tuple.getString(4);
        String syntax = tuple.getString(5);
        String post = tuple.getString(6);

        System.out.println("\nKey: " + key + "\nDate: " + date + "\nUser: " + user + "\nTitle: " + title + "\n" + post + "\nCount: " + count++);

//        sensitivityPredictor.predictSensitivity(tuple);
//        setSensitivity(sensitivityPredictor.getSensitivityLabel());
//        System.out.println("Sensitivity: "+sensitivity);
        collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("no-no"));
    }
}
