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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class TemporaryProcessBolt extends BaseRichBolt {

    OutputCollector collector;
    JSONParser parser = null;
    String type = "pastbin";

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
            String post = "";

            URL my_url2 = new URL(post_url);
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(my_url2.openStream()));

            while (bufferedReader2.ready()) {
                post += bufferedReader2.readLine();
            }

            collector.emit(tuple, new Values(type, key, date, user, title, post));

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        collector.ack(tuple);

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("type", "key", "date", "user", "title", "post"));
    }
}
