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

package bolt.core;

import model.ContentModel;
import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Content Classifier classifies each textual input into a set of classes. Each class is
 * defined by a template comprised of multiple checkpoints that evaluate the content.
 * The set of classes is pre-defined, and the list is not exhaustive as the categorization of
 * sensitive content is not comprehensive.
 *
 * Extend this class to classify text from different sources.
 *
 * @author Isuru Chandima
 */
public abstract class LeakHawkContentClassifier extends BaseRichBolt{

    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        prepareContentClassifier();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override ths method.
     *
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void prepareContentClassifier();

    @Override
    public void execute(Tuple tuple){
        Post post = (Post) tuple.getValue(0);

        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);

        executeContentClassifier(post, contentModel, tuple, collector);

        collector.ack(tuple);
    }

    /**
     * This method is called for each tuple in the bolt, all the functionality needs to
     * defined within the override method of executeContentClassifier in the sub class
     *
     * @param post Post object containing every detail of a single post
     * @param contentModel ContentModel object needs to store all outputs from content classifications
     * @param collector OutputCollector to emit output tuple after the execution
     */
    public abstract void executeContentClassifier(Post post, ContentModel contentModel, Tuple tuple, OutputCollector collector);

    /**
     * In the default application of Content classifier, only one output stream is declared
     * with one field "post" and no specific output stream.
     *
     * If different type of output streams are required according to the application,
     * override this method and declare output streams.
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
