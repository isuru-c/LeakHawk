import data.Post;
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
public class PostDownloadBolt extends BaseRichBolt {

    OutputCollector collector;
    JSONParser parser = null;

    String postType = "pastebin-posts";

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        parser = new JSONParser();
    }

    public void execute(Tuple tuple) {

        try {

            Object obj = parser.parse(tuple.getString(0));
            JSONObject postDetails = (JSONObject) obj;

            Post post = new Post();

            String postUrl = (String) postDetails.get("scrape_url");

            post.setPostType(postType);
            post.setKey((String) postDetails.get("key"));
            post.setDate((String) postDetails.get("date"));
            post.setTitle((String) postDetails.get("title"));
            post.setUser((String) postDetails.get("user"));
            post.setSyntax((String) postDetails.get("syntax"));

            String postText = "";

            URL my_url2 = new URL(postUrl);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(my_url2.openStream()));

            while (bufferedReader.ready()) {
                postText += bufferedReader.readLine();
            }

            post.setPostText(postText);

            collector.emit(tuple, new Values(post));

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
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
