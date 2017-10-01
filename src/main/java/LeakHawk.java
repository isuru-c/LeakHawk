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
import sensor.PastebinSensor;
import sensor.TwitterSensor;
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

//        TwitterSensor twitterSensor = new TwitterSensor();
//        twitterSensor.start();


         /* Testing sensor */

//        DumpSensor dumpSensor = new DumpSensor();
//        dumpSensor.start();

        // Create topology
        final String TOPOLOGY_NAME = "LeakHawk-topology";
        Config config = new Config();
        config.setMessageTimeoutSecs(120);
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // Create pastebin Spout and connect to the topology
        SpoutDeclarer pastebinSpout = topologyBuilder.setSpout("pastebin-spout", new PastebinSpout(), 2);

        // Create twitter Spout and connect to the topology
        SpoutDeclarer twitterSpout = topologyBuilder.setSpout("twitter-spout", new TwitterSpout(), 2);

        // PastebinPostDownload is used to get the content of a pastebin post
        BoltDeclarer pastebinPostDownload = topologyBuilder.setBolt("pastebin-post-download-bolt", new PastebinPostDownload() , 4);
        pastebinPostDownload.shuffleGrouping("pastebin-spout");

        // Both pastebin and twitter feeds are fed together to the pre-filter
        BoltDeclarer preFilter = topologyBuilder.setBolt("pre-filter-bolt", new PreFilter() , 3);
        preFilter.shuffleGrouping("pastebin-post-download-bolt");
        preFilter.shuffleGrouping("twitter-spout");

        // Both pastebin and twitter feeds are going through same context filter
        BoltDeclarer contextFilter = topologyBuilder.setBolt("context-filter-bolt", new ContextFilter() , 2);
        contextFilter.shuffleGrouping("pre-filter-bolt");

        // Separate evidence classifier for pastebin posts
        BoltDeclarer pastebinEvidenceClassifier = topologyBuilder.setBolt("pastebin-evidence-classifier-bolt", new PastebinEvidenceClassifier() , 1);
        pastebinEvidenceClassifier.shuffleGrouping("context-filter-bolt", "pastebin-out");

        // Separate evidence classifier for tweets
        BoltDeclarer tweetEvidenceClassifier = topologyBuilder.setBolt("tweets-evidence-classifier-bolt", new TweetEvidenceClassifier() , 1);
        tweetEvidenceClassifier.shuffleGrouping("context-filter-bolt", "tweets-out");

        // Separate content classifier for pastebin posts
        BoltDeclarer pastebinContentClassifier = topologyBuilder.setBolt("pastebin-content-classifier-bolt", new PastebinContentClassifier() , 1);
        pastebinContentClassifier.shuffleGrouping("pastebin-evidence-classifier-bolt");

        // Separate content classifier for tweets
        BoltDeclarer tweetContentClassifier = topologyBuilder.setBolt("tweets-content-classifier-bolt", new TweetContentClassifier() , 1);
        tweetContentClassifier.shuffleGrouping("tweets-evidence-classifier-bolt");

        // Both pastebin and twitter feeds are going through same synthesizer
        BoltDeclarer synthesizer = topologyBuilder.setBolt("synthesizer-bolt", new Synthesizer(), 1);
        synthesizer.shuffleGrouping("pastebin-content-classifier-bolt");
        synthesizer.shuffleGrouping("tweets-content-classifier-bolt");


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
