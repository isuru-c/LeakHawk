package spout;/*
 * Copyright 2017 SWIS
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

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Isuru Chandima on 9/11/17.
 */
public class DumpSpout extends BaseRichSpout{

    private SpoutOutputCollector collector;
    private Properties properties = null;
    private KafkaConsumer<String, String> consumer = null;

    private String postType = "dump-posts";

    public DumpSpout(){

        properties = new Properties();

        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "consumer-dump");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
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
            //System.out.println(record.offset() + ": " + record.value());

            Post post = new Post();

            post.setPostType(postType);
            post.setKey("");
            post.setDate("");
            post.setTitle("");
            post.setUser("");
            post.setSyntax("");
            post.setPostText(record.value());

            collector.emit(new Values(post));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
