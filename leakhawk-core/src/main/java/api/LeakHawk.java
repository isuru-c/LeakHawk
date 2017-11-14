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
import org.apache.storm.generated.KillOptions;
import org.apache.storm.generated.Nimbus;
import org.apache.storm.generated.TopologySummary;
import org.apache.storm.thrift.TException;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.SpoutDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.NimbusClient;
import org.apache.storm.utils.Utils;
import sensor.PastebinSensor;
import sensor.TwitterSensor;
import spout.DumpSpout;
import spout.PastebinSpout;
import spout.TwitterSpout;
import util.LeakHawkConstant;

import java.util.List;
import java.util.Map;

import static org.apache.storm.Config.TOPOLOGY_NAME;


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

//        PastebinSensor pastebinSensor = new PastebinSensor();
//        pastebinSensor.start();

//        /* Twitter sensor */
//
//        TwitterSensor twitterSensor = new TwitterSensor();
//        twitterSensor.start();
//
//
//         /* Testing sensor */
//
//        DumpSensor dumpSensor = new DumpSensor();
//        dumpSensor.start();

        // Create topology
        final String TOPOLOGY_NAME = "LeakHawk-topology";
        Config config = new Config();
        config.setMessageTimeoutSecs(120);

        LeakHawkConstant lp = new LeakHawkConstant();

        TopologyBuilder topologyBuilder = new TopologyBuilder();

        // Create pastebin Spout and connect to the topology
        SpoutDeclarer pastebinSpout = topologyBuilder.setSpout("pastebin-spout", new PastebinSpout(), 2);

        // Create twitter Spout and connect to the topology
        SpoutDeclarer twitterSpout = topologyBuilder.setSpout("twitter-spout", new TwitterSpout(), 2);

        // Create twitter Spout and connect to the topology
        // Use parameter LeakHawkConstant.POST_TYPE_PASTEBIN_POSTS to use dump posts as pastebin-posts
        // Use parameter LeakHawkConstant.POST_TYPE_TWEETS to use dump posts as tweets
        SpoutDeclarer dumpSpout = topologyBuilder.setSpout("dump-spout", new DumpSpout(LeakHawkConstant.POST_TYPE_PASTEBIN), 2);

        // PastebinPostDownload is used to get the content of a pastebin post
        BoltDeclarer pastebinPostDownload = topologyBuilder.setBolt("pastebin-post-download-bolt", new PastebinPostDownload(), 4);
        pastebinPostDownload.shuffleGrouping("pastebin-spout");

        // Separate pre filter for pastebin posts [ also the dump posts]
        BoltDeclarer pastebinPreFilter = topologyBuilder.setBolt("pastebin-pre-filter-bolt", new PastebinPreFilter(), 3);
        pastebinPreFilter.shuffleGrouping("pastebin-post-download-bolt", lp.P_POST_DOWNLOADER_TO_P_PRE_FILTER);
        pastebinPreFilter.shuffleGrouping("dump-spout", lp.DUMP_SPOUT_TO_P_PRE_FILTER);

        // Separate pre filter for tweets
        BoltDeclarer twitterPreFilter = topologyBuilder.setBolt("twitter-pre-filter", new TwitterPreFilter(), 3);
        twitterPreFilter.shuffleGrouping("twitter-spout");
        twitterPreFilter.shuffleGrouping("dump-spout", lp.DUMP_SPOUT_TO_T_PRE_FILTER);

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

//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                cluster.killTopology(TOPOLOGY_NAME);
//                cluster.shutdown();
//            }
//        });
    }


    public static void stopTopology() {
        try {
            Map conf = Utils.readStormConfig();
            Nimbus.Client client = NimbusClient.getConfiguredClient(conf).getClient();
            KillOptions killOpts = new KillOptions();
            //killOpts.set_wait_secs(waitSeconds); // time to wait before killing
            client.killTopologyWithOpts(TOPOLOGY_NAME, killOpts); //provide topology nam
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static boolean getTopologyState() {
        try {
            Map conf = Utils.readStormConfig();
            Nimbus.Client client = NimbusClient.getConfiguredClient(conf).getClient();
            List<TopologySummary> topologyList = client.getClusterInfo().get_topologies();
            boolean result = false;
            for(TopologySummary topology : topologyList){
                if(TOPOLOGY_NAME.equals(topology.get_name())){
                    result = true;
                    break;
                }
            }
            return result;

        } catch (TException e) {
            e.printStackTrace();
        }
        return false;
        // loop through the list and check if the required topology name is present in the list
        // if not it's not running
    }


    public static PastebinSensor startPastebinSensor(){
        PastebinSensor pastebinSensor = new PastebinSensor();
        pastebinSensor.start();
        return pastebinSensor;
    }

    public static TwitterSensor startTwitterSensor(){
        TwitterSensor twitterSensor = new TwitterSensor();
        twitterSensor.start();
        return twitterSensor;
    }



}
