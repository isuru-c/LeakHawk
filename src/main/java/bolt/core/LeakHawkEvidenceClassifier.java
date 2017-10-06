package bolt.core;

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

    protected OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    @Override
    public abstract void execute(Tuple tuple);

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
