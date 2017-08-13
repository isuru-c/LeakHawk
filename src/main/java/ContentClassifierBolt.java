import classifiers.Content.CCClassifier;
import classifiers.Content.ContentClassifier;
import classifiers.ContentModel;
import data.Post;
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


    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

        ccClassifier = new CCClassifier();

    }

    public void execute(Tuple tuple) {

        Post post = (Post)tuple.getValue(0);

        String title = post.getTitle();
        String postText = post.getPostText();

        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);

        boolean ccClassify = ccClassifier.classify(postText, title);
        boolean cfClassify = cfClassifier.classify(postText, title);
        boolean daClassify = daClassifier.classify(postText, title);
        boolean dbClassify = dbClassifier.classify(postText, title);
        boolean ecClassify = ecClassifier.classify(postText, title);
        boolean eoClassify = eoClassifier.classify(postText, title);
        boolean pkClassify = pkClassifier.classify(postText, title);

        contentModel.setPassedCC(ccClassify);
        contentModel.setPassedCF(cfClassify);
        contentModel.setPassedDA(daClassify);
        contentModel.setPassedDB(dbClassify);
        contentModel.setPassedEC(ecClassify);
        contentModel.setPassedEO(eoClassify);
        contentModel.setPassedPK(pkClassify);

        collector.emit(tuple, new Values(post));

        collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
