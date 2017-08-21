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

import classifiers.Content.*;
import classifiers.ContentModel;
import data.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class ContentClassifierBolt extends BaseRichBolt {

    OutputCollector collector;

    ContentClassifier ccClassifier;
    ContentClassifier cfClassifier;
    ContentClassifier daClassifier;
    ContentClassifier dbClassifier;
    ContentClassifier ecClassifier;
    ContentClassifier eoClassifier;
    ContentClassifier pkClassifier;


    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

        ccClassifier = new CCClassifier();
        cfClassifier = new CFClassifier();
        daClassifier = new DAClassifier();
        dbClassifier = new DBClassifier();
        ecClassifier = new ECClassifier();
        eoClassifier = new EOClassifier();
        pkClassifier = new PKClassifier();

    }

    public void execute(Tuple tuple) {


        Post post = (Post)tuple.getValue(0);
        String title = post.getTitle();
        String postText = post.getPostText();
        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);
        try {
            boolean ccClassify = ccClassifier.classify(postText, title);
            boolean cfClassify = cfClassifier.classify(postText, title);
            boolean daClassify = daClassifier.classify(postText, title);
            boolean dbClassify = dbClassifier.classify(postText, title);
            boolean ecClassify = ecClassifier.classify(postText, title);
            boolean eoClassify = eoClassifier.classify(postText, title);
            boolean pkClassify = pkClassifier.classify(postText, title);

            contentModel.setPassedCC(ccClassify);
            contentModel.setPassedCF(cfClassify);
            contentModel.setPassedDA(daClassify);
            contentModel.setPassedDB(dbClassify);
            contentModel.setPassedEC(ecClassify);
            contentModel.setPassedEO(eoClassify);
            contentModel.setPassedPK(pkClassify);

        }catch (java.lang.StackOverflowError e){
            contentModel.setPassedCC(false);
            contentModel.setPassedCF(false);
            contentModel.setPassedDA(false);
            contentModel.setPassedDB(false);
            contentModel.setPassedEC(false);
            contentModel.setPassedEO(false);
            contentModel.setPassedPK(false);

        }

        post.setContentClassifierPassed();
        collector.emit("ContentClassifier-out", tuple, new Values(post));
        collector.ack(tuple);

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //outputFieldsDeclarer.declare(new Fields("post"));
        outputFieldsDeclarer.declareStream("ContentClassifier-out", new Fields("post"));
    }
}
