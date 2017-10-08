package bolt.core;

import model.EvidenceModel;
import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * The objective of the Evidence Classifier is to identify whether the input
 * document indicates a sense of an attack or a sensitive information leakage.
 *
 * LeakHawkEvidenceClassifier needs to be extended according to various post types
 *
 * @author Isuru Chandima
 */
public abstract class LeakHawkEvidenceClassifier extends BaseRichBolt{

    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        prepareEvidenceClassifier();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override ths method.
     *
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void prepareEvidenceClassifier();

    @Override
    public void execute(Tuple tuple){
        Post post = (Post) tuple.getValue(0);

        EvidenceModel evidenceModel = new EvidenceModel();
        post.setEvidenceModel(evidenceModel);

        executeEvidenceClassifier(post, evidenceModel, tuple, collector);

        collector.ack(tuple);
    }

    /**
     * This method is called for each tuple in the bolt, all the functionality needs to
     * defined within the override method of executeEvidenceClassifier in the sub class
     *
     * @param post Post object containing every detail of a single post
     * @param evidenceModel EvidenceModel object needs to store all outputs from evidence classifications
     * @param tuple Tuple object received to this bolt
     * @param collector OutputCollector to emit output tuple after the execution
     */
    public abstract void executeEvidenceClassifier(Post post, EvidenceModel evidenceModel, Tuple tuple, OutputCollector collector);

    /**
     * In the default application of Evidence classifier, only one output stream is declared
     * with one field "post".
     *
     * If different type of output streams are required according to the application,
     * override this method and declare output streams.
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
