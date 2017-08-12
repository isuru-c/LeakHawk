import Classifiers.Content.CCClassifier;
import Classifiers.Content.ContentClassifier;
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
 * Created by Isuru Chandima on 7/28/17.
 */
public class ContentClassifierBolt extends BaseRichBolt {
    OutputCollector collector;
    ContentClassifier ccClassifier;
    ContentClassifier cfClassifier;
    ContentClassifier daClassifier;
    ContentClassifier dbClassifier;
    ContentClassifier ecClassifier;
    ContentClassifier eoClassifier;
    ContentClassifier pkClassifier;

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

        boolean ccClassify = ccClassifier.classify(post,title);
        boolean cfClassify = cfClassifier.classify(post, title);
        boolean daClassify = daClassifier.classify(post, title);
        boolean dbClassify = dbClassifier.classify(post, title);
        boolean ecClassify = ecClassifier.classify(post, title);
        boolean eoClassify = eoClassifier.classify(post, title);
        boolean pkClassify = pkClassifier.classify(post, title);


        ContentModel contentModel = new ContentModel();
        contentModel.setPassedCC(ccClassify);
        contentModel.setPassedCF(cfClassify);
        contentModel.setPassedDA(daClassify);
        contentModel.setPassedDB(dbClassify);
        contentModel.setPassedEC(ecClassify);
        contentModel.setPassedEO(eoClassify);
        contentModel.setPassedPK(pkClassify);

        collector.emit(tuple, new Values(type, key, date, user, title, syntax, post,contentModel));

        collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("type", "key", "date", "user", "title", "syntax", "post"));
    }
}
