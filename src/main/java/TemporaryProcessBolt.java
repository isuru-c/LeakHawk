import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class TemporaryProcessBolt extends BaseRichBolt{

    OutputCollector collector;
    JSONParser parser = null;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        parser = new JSONParser();
    }

    public void execute(Tuple tuple) {

        try {

            Object obj = parser.parse(tuple.getString(0));
            JSONObject post_details = (JSONObject) obj;

            String post_url = (String) post_details.get("scrape_url");
            String key = (String) post_details.get("key");
            String date = (String) post_details.get("date");
            String title = (String) post_details.get("title");
            String user = (String) post_details.get("user");

            collector.emit(tuple, new Values(post_url, key, date, title, user));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        collector.ack(tuple);

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("scrape_url", "key", "date", "title", "user"));
    }
}
