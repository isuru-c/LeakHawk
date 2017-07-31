import org.apache.logging.log4j.util.Strings;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class EvidenceClassifierBolt extends BaseRichBolt{

    OutputCollector collector;

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

        if(!isContainKeyWord(post)) {
            collector.emit(tuple, new Values(type, key, date, user, title, syntax, post));
        }

        collector.ack(tuple);
    }

    private boolean isContainKeyWord(String post) {
        ArrayList<String> keyWordList = new ArrayList(Arrays.asList("Hacked","leaked by","Pwned by","Doxed","Ow3ned","pawned by","Server Rootéd","#opSriLanka","#OPlanka","#anonymous","Private key","Password leak","password dump","credential leak","credential dump","Credit card","Card dump "," cc dump "," credit_card","card_dump","working_card","cc_dump","skimmed","card_hack","sited hacked by","websited hacked by","website hacked by","site hacked by","websited hacked","domain hack","defaced","leaked by","site deface","mass deface","database dump","database dumped","db dumped","db_dump","db leak","data base dump","data base leak","database hack","db hack","login dump","DNS LeAkEd","DNS fuck3d","zone transfer","DNS Enumeration","Enumeration Attack","cache snooping","cache poisoning","email hack","emails hack","emails leak,email leak","email dump","emails dump","email dumps","email-list","leaked email,leaked emails","email_hack"));


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
