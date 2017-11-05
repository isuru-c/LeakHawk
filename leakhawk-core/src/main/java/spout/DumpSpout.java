/*
 *    Copyright 2017 SWIS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package spout;

import model.Post;
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
import util.LeakHawkConstant;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * This Spout will get the data from kafka and connect it to the storm topology
 *
 * @author Isuru Chandima
 */
public class DumpSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private Properties properties = null;
    private KafkaConsumer<String, String> consumer = null;

    private String postType = "dump-posts";

    public DumpSpout(String postType) {
        properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "consumer-dump");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());

        this.postType = postType;
    }

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList("dump-posts"));
    }

    public void nextTuple() {
        Utils.sleep(100);
        ConsumerRecords<String, String> records = consumer.poll(1000);
        for (ConsumerRecord<String, String> record : records) {
            Post post = new Post();
            post.setPostType(postType);
            post.setKey("");
            post.setDate("");
            post.setTitle("");
            post.setUser("");
            post.setSyntax("");
            post.setLanguage("en");
            post.setPostText(record.value());
            if(postType.equals(LeakHawkConstant.POST_TYPE_PASTEBIN)) {
                collector.emit(LeakHawkConstant.DUMP_SPOUT_TO_P_PRE_FILTER, new Values(post));
            }else if(postType.equals(LeakHawkConstant.POST_TYPE_TWEETS)){
                collector.emit(LeakHawkConstant.DUMP_SPOUT_TO_T_PRE_FILTER, new Values(post));
            }
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(LeakHawkConstant.DUMP_SPOUT_TO_P_PRE_FILTER, new Fields("post"));
        outputFieldsDeclarer.declareStream(LeakHawkConstant.DUMP_SPOUT_TO_T_PRE_FILTER, new Fields("post"));
    }
}
