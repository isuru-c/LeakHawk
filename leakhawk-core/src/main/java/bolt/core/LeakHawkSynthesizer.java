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

import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import util.LeakHawkParameters;

import java.util.Map;

/**
 * This class is used to predict the sensitive level and risk of a post by using
 * classification done by previous content and evidence classifiers
 *
 * @author Isuru Chandima
 */
public abstract class LeakHawkSynthesizer extends BaseRichBolt{

    protected OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        prepareSynthesizer();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override ths method.
     *
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void prepareSynthesizer();

    @Override
    public void execute(Tuple tuple){
        Post post = (Post) tuple.getValue(0);

        executeSynthesizer(post, tuple, collector);

        collector.ack(tuple);
    }

    /**
     * This method is called for each tuple in the bolt, all the functionality needs to
     * defined within the override method of executeSynthersizer in the sub class
     *
     * @param post Post object containing every detail of a single post
     * @param tuple Tuple object received to this bolt
     * @param collector OutputCollector to emit output tuple after the execution
     */
    public abstract void executeSynthesizer(Post post, Tuple tuple, OutputCollector collector);

    /**
     * In the default application of Synthesizer, no output stream is required since
     * it is the last bolt. No further processes after this bolt.
     * In here a dump output stream is declared with one field "no-no".
     *
     * If different type of output streams are required according to the application,
     * override this method and declare output streams.
     *
     * It is necessary to call for super.declareOutputFields() method if statics
     * are collecting in the new bolt
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(LeakHawkParameters.STATICS_FLOW, new Fields("statics"));
        outputFieldsDeclarer.declare(new Fields("no-no"));
    }
}
