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
 * The primary objective of the PRE filter is to screen-out the inputs,
 * which are non-sensitive in nature, such as video game chat sessions,
 * pornographic content, and torrent information.
 *
 * This bolt can be extended to use for any specific type of text data
 *
 * OutputCollector is defined in this class and, default output field has
 * been declared as "post". Override those methods if it is required to change
 *
 * @author Isuru Chandima
 */
public abstract class LeakHawkPreFilter extends BaseRichBolt{

    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        preparePreFilter();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override ths method.
     *
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void preparePreFilter();

    @Override
    public void execute(Tuple tuple){
        Post post = (Post) tuple.getValue(0);

        executePreFilter(post, tuple, collector);

        collector.ack(tuple);
    }

    /**
     * This method is called for each tuple in the bolt, all the functionality needs to
     * defined within the override method of executePreFilter in the sub class
     *
     * @param post Post object containing every detail of a single post
     * @param tuple Tuple object received to this bolt
     * @param collector OutputCollector to emit output tuple after the execution
     */
    public abstract void executePreFilter(Post post, Tuple tuple, OutputCollector collector);

    /**
     * In the default application of Pre Filter, only one output stream is declared
     * with one field "post" and no specific output stream.
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
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
