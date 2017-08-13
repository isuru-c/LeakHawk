import data.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class PreFilterBolt extends BaseRichBolt {

    OutputCollector collector;
    ArrayList keyWordList;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

        keyWordList = new ArrayList<String>();
        keyWordList.add("Game");
        keyWordList.add("Sports");
        keyWordList.add("Porn");
        keyWordList.add("Sex");
        keyWordList.add("XXX");
    }

    public void execute(Tuple tuple) {

        Post post = (Post)tuple.getValue(0);

        //if pre filter is passed forward the data to next bolt(context filter)
        if(!isContainKeyWord(post.getPostText())) {
            collector.emit(tuple, new Values(post));
        }else{
//            System.out.println("\nUser: " + post.getUser() + "\nTitle: " + post.getTitle() + "\n" + post.getPostText() + "\n--- Filtered out by pre filter ---\n");
        }
        collector.ack(tuple);

    }

    private boolean isContainKeyWord(String post) {

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
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
