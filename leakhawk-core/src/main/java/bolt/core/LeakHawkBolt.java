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

import model.Statics;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import util.LeakHawkConstant;

import java.util.ArrayList;
import java.util.Map;

/**
 * This is the base class of LeakHawk topology. All generic things are defined in this
 * class and can be extended to use more functionality.
 * <p>
 * Statics counting is fully defined in this class. Any bolt which required statics to
 * be counted and send to a special node needs to extend this class.
 *
 * @author Isuru Chandima
 */
public abstract class LeakHawkBolt extends BaseRichBolt {

    protected OutputCollector collector;
    private long startTime;
    private int inCount;
    private int outCount;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        startTime = System.currentTimeMillis();
        inCount = 0;
        outCount = 0;

        prepareBolt();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override ths method.
     * <p>
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void prepareBolt();

    /**
     * This method needs to be override in every child class to get the name of the class.
     * Name is required when sending statics to the aggregation node to identify the
     * source of the statics.
     *
     * @return a string containing the name of the filter or classifier of current bolt
     */
    protected abstract String getBoltName();

    /**
     * Used to get the total number of tuple/posts processed by this bolt including
     * positively and negatively classified posts.
     *
     * @return total number of posts processed by this bolt
     */
    private int getInCount() {
        return inCount;
    }

    /**
     * Used to get the count of tuple/posts emitted by this bolt as positive classified
     * of filtered in to the next bolt.
     *
     * @return positively classified number of posts
     */
    private int getOutCount() {
        return outCount;
    }

    /**
     * Increase the inCount variable by one.
     * Used to count the number of posts processed by single bolt
     */
    protected void increaseInCount(){
        this.inCount++;
    }

    /**
     * Increase the outCount variable by one
     * Used to count the number of posts classified as positive by single bolt
     */
    protected void increaseOutCount(){
        this.outCount++;
    }

    /**
     * Reset both inCount and outCount variables
     * Used when periodically statics are send to the aggregation bolt
     */
    private void resetCount(){
        this.inCount=0;
        this.outCount=0;
    }

    protected void runCounter() {
        long currentTime = System.currentTimeMillis();

        if (((currentTime - startTime) / 1000) > LeakHawkConstant.STATICS_UPDATE_INTERVAL) {
            startTime = currentTime;

            Statics statics = new Statics(getBoltName(), getInCount(), getOutCount());
            resetCount();
            collector.emit(LeakHawkConstant.STATICS_FLOW, new Values(statics));
        }
    }

    @Override
    public abstract void execute(Tuple tuple);

    /**
     * In the default application of LeakHawkFilter, only one specific output stream
     * LeakHawkConstant.STATICS_FLOW is dedicated for aggregating statics for statics counter
     * <p>
     * If different type of output streams are required according to the application,
     * override declareOutputStreams method and declare output streams.
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(LeakHawkConstant.STATICS_FLOW, new Fields("statics"));

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
