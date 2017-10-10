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
import bolt.pastebin.PastebinContentClassifier;
import bolt.pastebin.PastebinEvidenceClassifier;
import bolt.pastebin.PastebinPostDownload;
import bolt.pastebin.PastebinPreFilter;
import bolt.twitter.TweetContentClassifier;
import bolt.twitter.TweetEvidenceClassifier;
import bolt.twitter.TwitterPreFilter;
import exception.LeakHawkTopologyException;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.SpoutDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import sensor.DumpSensor;
import spout.DumpSpout;
import spout.PastebinSpout;
import spout.TwitterSpout;
import util.LeakHawkParameters;

/**
 * This is the main class of the system. running main method in here will start to run the storm topology.
 *
 * @author Isuru Chandima
 * @author Sugeesh Chandraweera
 */
public class LeakHawk {

    public static void main(String[] args) {
        startLeakhawk();
    }


    public static void startLeakhawk() {
        /* Pastebin sensor */

        //PastebinSensor pastebinSensor = new PastebinSensor();
        //pastebinSensor.start();

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

        LeakHawkParameters lp = new LeakHawkParameters();

        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // Create pastebin Spout and connect to the topology
        SpoutDeclarer pastebinSpout = topologyBuilder.setSpout("pastebin-spout", new PastebinSpout(), 2);

        // Create twitter Spout and connect to the topology
        SpoutDeclarer twitterSpout = topologyBuilder.setSpout("twitter-spout", new TwitterSpout(), 2);

        // Create twitter Spout and connect to the topology
        SpoutDeclarer dumpSpout = topologyBuilder.setSpout("dump-spout", new DumpSpout(), 2);

        // PastebinPostDownload is used to get the content of a pastebin post
        BoltDeclarer pastebinPostDownload = topologyBuilder.setBolt("pastebin-post-download-bolt", new PastebinPostDownload(), 4);
        pastebinPostDownload.shuffleGrouping("pastebin-spout");

        // Separate pre filter for pastebin posts [ also the dump posts]
        BoltDeclarer pastebinPreFilter = topologyBuilder.setBolt("pastebin-pre-filter-bolt", new PastebinPreFilter(), 3);
        pastebinPreFilter.shuffleGrouping("pastebin-post-download-bolt");
        pastebinPreFilter.shuffleGrouping("dump-spout");

        // Separate pre filter for tweets
        BoltDeclarer twitterPreFilter = topologyBuilder.setBolt("twitter-pre-filter", new TwitterPreFilter(), 3);
        twitterPreFilter.shuffleGrouping("twitter-spout");
        //twitterPreFilter.shuffleGrouping("dump-spout");

        // Both pastebin and twitter feeds are going through same context filter
        BoltDeclarer contextFilter = topologyBuilder.setBolt("context-filter-bolt", new ContextFilter(), 2);
        contextFilter.shuffleGrouping("pastebin-pre-filter-bolt", lp.P_PRE_FILTER_TO_CONTEXT_FILTER);
        contextFilter.shuffleGrouping("twitter-pre-filter", lp.T_PRE_FILTER_TO_CONTEXT_FILTER);

        // Separate evidence classifier for pastebin posts
        BoltDeclarer pastebinEvidenceClassifier = topologyBuilder.setBolt("pastebin-evidence-classifier-bolt", new PastebinEvidenceClassifier(), 1);
        pastebinEvidenceClassifier.shuffleGrouping("context-filter-bolt", lp.CONTEXT_FILTER_TO_P_EVIDENCE_CLASSIFIER);

        // Separate evidence classifier for tweets
        BoltDeclarer tweetEvidenceClassifier = topologyBuilder.setBolt("tweets-evidence-classifier-bolt", new TweetEvidenceClassifier(), 1);
        tweetEvidenceClassifier.shuffleGrouping("context-filter-bolt", lp.CONTEXT_FILTER_TO_T_EVIDENCE_CLASSIFIER);

        // Url Processor for both pastebin posts and tweets
        BoltDeclarer urlProcessor = topologyBuilder.setBolt("url-processor", new UrlProcessor(), 3);
        urlProcessor.shuffleGrouping("pastebin-evidence-classifier-bolt", lp.P_EVIDENCE_CLASSIFIER_TO_URL_PROCESSOR);
        urlProcessor.shuffleGrouping("tweets-evidence-classifier-bolt", lp.T_EVIDENCE_CLASSIFIER_TO_URL_PROCESSOR);

        // Separate content classifier for pastebin posts
        BoltDeclarer pastebinContentClassifier = topologyBuilder.setBolt("pastebin-content-classifier-bolt", new PastebinContentClassifier(), 1);
        pastebinContentClassifier.shuffleGrouping("pastebin-evidence-classifier-bolt", lp.P_EVIDENCE_CLASSIFIER_TO_P_CONTENT_CLASSIFIER);
        pastebinContentClassifier.shuffleGrouping("url-processor", lp.URL_PROCESSOR_TO_P_CONTENT_CLASSIFIER);

        // Separate content classifier for tweets
        BoltDeclarer tweetContentClassifier = topologyBuilder.setBolt("tweets-content-classifier-bolt", new TweetContentClassifier(), 1);
        tweetContentClassifier.shuffleGrouping("tweets-evidence-classifier-bolt", lp.T_EVIDENCE_CLASSIFIER_TO_T_CONTENT_CLASSIFIER);
        tweetContentClassifier.shuffleGrouping("url-processor", lp.URL_PROCESSOR_TO_T_CONTENT_CLASSIFIER);

        // Both pastebin and twitter feeds are going through same synthesizer
        BoltDeclarer synthesizer = topologyBuilder.setBolt("synthesizer-bolt", new Synthesizer(), 1);
        synthesizer.shuffleGrouping("pastebin-content-classifier-bolt", lp.P_CONTENT_CLASSIFIER_TO_SYNTHESIZER);
        synthesizer.shuffleGrouping("tweets-content-classifier-bolt", lp.T_CONTENT_CLASSIFIER_TO_SYNTHESIZER);

        BoltDeclarer staticsCounter = topologyBuilder.setBolt("statics-counter-bolt", new StaticsCounter(), 1);
        staticsCounter.shuffleGrouping("pastebin-pre-filter-bolt", lp.STATICS_FLOW);
        staticsCounter.shuffleGrouping("twitter-pre-filter", lp.STATICS_FLOW);
        staticsCounter.shuffleGrouping("context-filter-bolt", lp.STATICS_FLOW);
        staticsCounter.shuffleGrouping("pastebin-evidence-classifier-bolt", lp.STATICS_FLOW);
        staticsCounter.shuffleGrouping("tweets-evidence-classifier-bolt", lp.STATICS_FLOW);
        staticsCounter.shuffleGrouping("pastebin-content-classifier-bolt", lp.STATICS_FLOW);
        staticsCounter.shuffleGrouping("tweets-content-classifier-bolt", lp.STATICS_FLOW);

        final LocalCluster cluster = new LocalCluster();

        try {
            cluster.submitTopology(TOPOLOGY_NAME, config, topologyBuilder.createTopology());
        } catch (Exception exception) {
            throw new LeakHawkTopologyException("Topology build failed", exception);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cluster.killTopology(TOPOLOGY_NAME);
                cluster.shutdown();
            }
        });
    }

}
