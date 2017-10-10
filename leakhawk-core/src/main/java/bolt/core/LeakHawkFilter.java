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
import org.apache.storm.tuple.Values;
import util.LeakHawkParameters;

import java.util.ArrayList;
import java.util.Map;

/**
 * This bolt can be extended to use for any type of filtering of post text data
 * <p>
 * OutputCollector is defined in this class and, no output stream has been
 * declared except statics aggregation flow. Override declareOutputStreams method
 * and use it to define application specific output streams.
 * <p>
 * isFilterPassed method needs to be override and use it for the filtering process
 *
 * @author Isuru Chandima
 */
public abstract class LeakHawkFilter extends BaseRichBolt {

    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        prepareFilter();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override ths method.
     * <p>
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void prepareFilter();

    @Override
    public void execute(Tuple tuple) {
        Post post = (Post) tuple.getValue(0);

        if (isFilterPassed(post)) {
            collector.emit(post.getNextOutputStream(), tuple, new Values(post));
        }

        collector.ack(tuple);
    }

    /**
     * This method is called once for each post. Do necessary processing and return
     * true or false to forward the post more or not.
     * <p>
     * It is necessary to indicate next output stream in the post object if it is
     * going to forward further in LeakHawk core system.
     *
     * @param post Post object containing every detail of a single post
     * @return true if passed from the filter. false otherwise.
     */
    public abstract boolean isFilterPassed(Post post);

    /**
     * In the default application of LeakHawkFilter, only one specific output stream
     * LeakHawkParameters.STATICS_FLOW is dedicated for aggregating statics for statics counter
     * <p>
     * If different type of output streams are required according to the application,
     * override declareOutputStreams method and declare output streams.
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(LeakHawkParameters.STATICS_FLOW, new Fields("statics"));

        ArrayList<String> outputStreams = declareOutputStreams();

        for (String outputStream : outputStreams) {
            outputFieldsDeclarer.declareStream(outputStream, new Fields("post"));
        }

    }

    /**
     * If different type of output streams are required according to the application,
     * override declareOutputStreams method and declare output streams.
     * <p>
     * Needs to return an array of String containing the names of output streams
     *
     * @return ArrayList of Strings containing names of required output streams
     */
    public abstract ArrayList<String> declareOutputStreams();
}
