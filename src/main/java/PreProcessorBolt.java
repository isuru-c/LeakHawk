/*
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
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.text.Normalizer;
import java.util.Map;

/**
 * Created by Isuru Chandima on 9/1/17.
 */
public class PreProcessorBolt extends BaseRichBolt{

    private OutputCollector collector;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    public void execute(Tuple tuple) {
        Post post = (Post)tuple.getValue(0);

        String postText = post.getPostText();

        postText = Normalizer.normalize(postText, Normalizer.Form.NFD);

        postText = postText.toLowerCase();

        //norm = norm.replaceAll("[^a-zA-Z0-9]", " ");
        //norm = norm.replaceAll("\\s+", " ");
        postText = postText.replaceAll("\\<[^>]*>","");
        postText = postText.replaceAll("[^a-zA-Z0-9.]", " ");
        postText = postText.replaceAll("\\s+", " ");
        //norm = norm.replaceAll("[^\\p{ASCII}]", ""); // drop non latin

        post.setPostText(postText);

        collector.emit(tuple, new Values(post));

        collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
