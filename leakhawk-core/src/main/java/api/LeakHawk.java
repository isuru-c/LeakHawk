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

package api;
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
        startLeakhawk();

    }


    public static void startLeakhawk(){
        /* Pastebin sensor */

        PastebinSensor pastebinSensor = new PastebinSensor();
        pastebinSensor.start();

        /* Twitter sensor */

        //TwitterSensor twitterSensor = new TwitterSensor();
        //twitterSensor.start();


         /* Testing sensor */

        DumpSensor dumpSensor = new DumpSensor();
        dumpSensor.start();

        // Create topology
        final String TOPOLOGY_NAME = "LeakHawk-topology";
        Config config = new Config();
        config.setMessageTimeoutSecs(120);
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // Create pastebin Spout and connect to the topology
        SpoutDeclarer pastebinSpout = topologyBuilder.setSpout("pastebin-spout", new PastebinSpout(), 2);

        // Create twitter Spout and connect to the topology
        SpoutDeclarer twitterSpout = topologyBuilder.setSpout("twitter-spout", new TwitterSpout(), 2);

        // Create twitter Spout and connect to the topology
        SpoutDeclarer dumpSpout = topologyBuilder.setSpout("dump-spout", new DumpSpout(), 2);

        // PastebinPostDownload is used to get the content of a pastebin post
        BoltDeclarer pastebinPostDownload = topologyBuilder.setBolt("pastebin-post-download-bolt", new PastebinPostDownload() , 4);
        pastebinPostDownload.shuffleGrouping("pastebin-spout");

        // Separate pre filter for pastebin posts [ also the dump posts]
        BoltDeclarer pastebinPreFilter = topologyBuilder.setBolt("pastebin-pre-filter-bolt", new PastebinPreFilter() , 3);
        pastebinPreFilter.shuffleGrouping("pastebin-post-download-bolt");
        pastebinPreFilter.shuffleGrouping("dump-spout");

        // Separate pre filter for tweets
        BoltDeclarer twitterPreFilter = topologyBuilder.setBolt("twitter-pre-filter", new TwitterPreFilter(), 3);
        twitterPreFilter.shuffleGrouping("twitter-spout");
        //twitterPreFilter.shuffleGrouping("dump-spout");

        // Both pastebin and twitter feeds are going through same context filter
        BoltDeclarer contextFilter = topologyBuilder.setBolt("context-filter-bolt", new ContextFilter() , 2);
        contextFilter.shuffleGrouping("pastebin-pre-filter-bolt");
        contextFilter.shuffleGrouping("twitter-pre-filter");

        // Separate evidence classifier for pastebin posts
        BoltDeclarer pastebinEvidenceClassifier = topologyBuilder.setBolt("pastebin-evidence-classifier-bolt", new PastebinEvidenceClassifier() , 1);
        pastebinEvidenceClassifier.shuffleGrouping("context-filter-bolt", "context-filter-pastebin-out");

        // Separate evidence classifier for tweets
        BoltDeclarer tweetEvidenceClassifier = topologyBuilder.setBolt("tweets-evidence-classifier-bolt", new TweetEvidenceClassifier() , 1);
        tweetEvidenceClassifier.shuffleGrouping("context-filter-bolt", "context-filter-tweets-out");

        // Url Processor for both pastebin posts and tweets
        BoltDeclarer urlProcessor = topologyBuilder.setBolt("url-processor", new UrlProcessor(), 3);
        urlProcessor.shuffleGrouping("pastebin-evidence-classifier-bolt", "pastebin-url-flow");
        urlProcessor.shuffleGrouping("tweets-evidence-classifier-bolt", "tweets-url-flow");

        // Separate content classifier for pastebin posts
        BoltDeclarer pastebinContentClassifier = topologyBuilder.setBolt("pastebin-content-classifier-bolt", new PastebinContentClassifier() , 1);
        pastebinContentClassifier.shuffleGrouping("pastebin-evidence-classifier-bolt", "pastebin-normal-flow");
        pastebinContentClassifier.shuffleGrouping("url-processor", "url-processor-pastebin-out");

        // Separate content classifier for tweets
        BoltDeclarer tweetContentClassifier = topologyBuilder.setBolt("tweets-content-classifier-bolt", new TweetContentClassifier() , 1);
        tweetContentClassifier.shuffleGrouping("tweets-evidence-classifier-bolt", "tweets-normal-flow");
        tweetContentClassifier.shuffleGrouping("url-processor","url-processor-tweets-out");

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
