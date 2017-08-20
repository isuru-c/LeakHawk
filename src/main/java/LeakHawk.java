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

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.SpoutDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import sensors.PastebinSensor;

/**
 * Created by Isuru Chandima on 6/18/17.
 */
public class LeakHawk {

    public static void main(String[] args) {

        PastebinSensor pastebinSensor = new PastebinSensor();
        pastebinSensor.start();

        final String TOPOLOGY_NAME = "LeakHawk-topology";

        Config config = new Config();
        config.setMessageTimeoutSecs(120);

        TopologyBuilder builder = new TopologyBuilder();

        SpoutDeclarer pastebinSpout = builder.setSpout("pastebin-spout", new PastebinSpout(), 2);

        BoltDeclarer postDownloadBolt = builder.setBolt("post-download", new PostDownloadBolt() , 4);
        postDownloadBolt.shuffleGrouping("pastebin-spout");

        BoltDeclarer preFilterBolt = builder.setBolt("pre-filter", new PreFilterBolt() , 3);
        preFilterBolt.shuffleGrouping("post-download");

        BoltDeclarer contextFilterBolt = builder.setBolt("context-filter", new ContextFilterBolt() , 2);
        contextFilterBolt.shuffleGrouping("pre-filter");

        BoltDeclarer evidenceClassifierBolt = builder.setBolt("evidence-classifier", new EvidenceClassifierBolt() , 1);
        evidenceClassifierBolt.shuffleGrouping("context-filter","EvidenceClassifier-in");

        BoltDeclarer contentClassifierBolt = builder.setBolt("content-classifier", new ContentClassifierBolt() , 1);
        contentClassifierBolt.shuffleGrouping("context-filter","ContentClassifier-in");

        BoltDeclarer evidenceContentJoinBolt = builder.setBolt("evidence-content-join", new EvidenceContentJoinBolt(), 1);
        evidenceContentJoinBolt.globalGrouping("evidence-classifier", "EvidenceClassifier-out");
        evidenceContentJoinBolt.globalGrouping("content-classifier", "ContentClassifier-out");

        BoltDeclarer synthesizerBolt = builder.setBolt("synthesizer", new Synthesizer(), 1);
        synthesizerBolt.shuffleGrouping("evidence-content-join");

        final LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cluster.killTopology(TOPOLOGY_NAME);
                cluster.shutdown();
            }
        });

    }

}
