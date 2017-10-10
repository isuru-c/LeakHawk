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

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import util.LeakHawkParameters;

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

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        prepareBolt();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override ths method.
     * <p>
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void prepareBolt();

    @Override
    public abstract void execute(Tuple tuple);

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
