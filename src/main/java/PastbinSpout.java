import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class PastbinSpout extends BaseRichSpout{

    private SpoutOutputCollector collector;
    Properties properties = null;
    KafkaConsumer<String, String> consumer = null;

    public PastbinSpout(){

        properties = new Properties();

        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "consumer-test");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());

    }

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;

        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList("pastbin-posts"));
    }

    public void nextTuple() {
        Utils.sleep(100);

        ConsumerRecords<String, String> records = consumer.poll(1000);
        for (ConsumerRecord<String, String> record : records) {
            //System.out.println(record.offset() + ": " + record.value());
            collector.emit(new Values(record.value()));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
