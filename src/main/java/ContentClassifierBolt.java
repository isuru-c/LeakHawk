import Classifiers.Content.CCClassifier;
import Classifiers.ContentModel;
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
public class ContentClassifierBolt extends BaseRichBolt {
    OutputCollector collector;
    CCClassifier ccClassifier;

    public void setContentClassifierPassed(boolean contentClassifierPassed) {
        this.contentClassifierPassed = contentClassifierPassed;
    }

    public boolean isContentClassifierPassed() {
        return contentClassifierPassed;
    }

    private boolean contentClassifierPassed = false; //needed for sensitivity prediction

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        ccClassifier = new CCClassifier();
    }

    public void execute(Tuple tuple) {

        String type = tuple.getString(0);
        String key = tuple.getString(1);
        String date = tuple.getString(2);
        String user = tuple.getString(3);
        String title = tuple.getString(4);
        String syntax = tuple.getString(5);
        String post = tuple.getString(6);

        boolean ccClassify = ccClassifier.classify(post);



        ContentModel contentModel = new ContentModel();
        contentModel.setPassedCC(ccClassify);

        collector.emit(tuple, new Values(type, key, date, user, title, syntax, post));

        collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("type", "key", "date", "user", "title", "syntax", "post"));
    }
}
