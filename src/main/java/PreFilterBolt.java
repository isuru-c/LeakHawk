import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class PreFilterBolt extends BaseRichBolt {

    OutputCollector collector;
    static int count = 0;

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


        //System.out.println("\nKey: " + key + "\nDate: " + date + "\nUser: " + user + "\nTitle: " + title + "\n" + post + "\nCount: " + count++);
        if(!isContainKeyWord(post)) {
            collector.emit(tuple, new Values(type, key, date, user, title, syntax, post));
        }

        collector.ack(tuple);

    }

    private boolean isContainKeyWord(String post) {
        ArrayList keyWordList = new ArrayList<String>();
        keyWordList.add("Game");
        keyWordList.add("Sports");

        try {

            for (int i=0;i<keyWordList.size();i++) {
                if (post.toUpperCase().contains(keyWordList.get(i).toString().toUpperCase())) {
                    //exit after the first successful hit
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("type", "key", "date", "user", "title", "syntax", "post"));
    }
}
