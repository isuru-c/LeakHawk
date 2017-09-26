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

import bolt.*;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.SpoutDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import sensor.DumpSensor;
import sensor.PastebinSensor;
import sensor.TwitterSensor;
import spout.DumpSpout;
import spout.PastebinSpout;
import spout.TwitterSpout;

/**
 *
 * This is the main class of the system. running main method in here will start to run the storm topology.
 *
 * @author Isuru Chandima
 * @author Sugeesh Chandraweera
 */
public class LeakHawk {

    public static void main(String[] args) {

        /* Pastebin sensor */
        PastebinSensor pastebinSensor = new PastebinSensor();
        pastebinSensor.start();

        /* Twitter sensor */
/*
        TwitterSensor twitterSensor = new TwitterSensor();
        twitterSensor.start();
*/

         /* Testing sensor */
//        DumpSensor dumpSensor = new DumpSensor();
//        dumpSensor.start();

        // Create topology
        final String TOPOLOGY_NAME = "LeakHawk-topology";
        Config config = new Config();
        config.setMessageTimeoutSecs(120);
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // Create Spout and connect to the topology
        SpoutDeclarer pastebinSpout = topologyBuilder.setSpout("pastebin-spout", new PastebinSpout(), 2);
//        SpoutDeclarer dumpSpout = topologyBuilder.setSpout("dump-spout", new DumpSpout(), 1);
//        SpoutDeclarer twitterSpout = topologyBuilder.setSpout("twitter-spout", new TwitterSpout(), 1);


        // Create bolts and connect to the topology
        BoltDeclarer postDownloadBolt = topologyBuilder.setBolt("pastebin-post-download-bolt", new PostDownloadBolt() , 4);
        postDownloadBolt.shuffleGrouping("pastebin-spout");

        BoltDeclarer preFilterBolt = topologyBuilder.setBolt("pre-filter-bolt", new PreFilterBolt() , 3);
        preFilterBolt.shuffleGrouping("pastebin-post-download-bolt");
//        preProcessorBolt.shuffleGrouping("dump-spout");
//        preProcessorBolt.shuffleGrouping("twitter-spout");


        BoltDeclarer contextFilterBolt = topologyBuilder.setBolt("context-filter-bolt", new ContextFilterBolt() , 2);
        contextFilterBolt.shuffleGrouping("pre-filter-bolt");

        BoltDeclarer evidenceClassifierBolt = topologyBuilder.setBolt("evidence-classifier-bolt", new EvidenceClassifierBolt() , 1);
        evidenceClassifierBolt.shuffleGrouping("context-filter-bolt");
        //evidenceClassifierBolt.shuffleGrouping("context-filter","EvidenceClassifier-in");

        BoltDeclarer contentClassifierBolt = topologyBuilder.setBolt("content-classifier-bolt", new ContentClassifierBolt() , 1);
        contentClassifierBolt.shuffleGrouping("evidence-classifier-bolt");
        //contentClassifierBolt.shuffleGrouping("context-filter","ContentClassifier-in");

        //BoltDeclarer evidenceContentJoinBolt = builder.setBolt("evidence-content-join", new bolt.EvidenceContentJoinBolt(), 1);
        //evidenceContentJoinBolt.globalGrouping("evidence-classifier", "EvidenceClassifier-out");
        //evidenceContentJoinBolt.globalGrouping("content-classifier", "ContentClassifier-out");

        BoltDeclarer synthesizerBolt = topologyBuilder.setBolt("synthesizer-bolt", new Synthesizer(), 1);
        synthesizerBolt.shuffleGrouping("content-classifier-bolt");
        //synthesizerBolt.shuffleGrouping("evidence-content-join");

        final LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(TOPOLOGY_NAME, config, topologyBuilder.createTopology());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cluster.killTopology(TOPOLOGY_NAME);
                cluster.shutdown();
            }
        });

    }

}
