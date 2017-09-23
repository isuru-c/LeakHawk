package bolt;/*
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
import model.ContentModel;
import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class ContentClassifierBolt extends BaseRichBolt {

    private OutputCollector collector;
    private ArrayList<ContentClassifier> classifierList;


    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        classifierList = new ArrayList<ContentClassifier>();

        Reflections reflections = new Reflections("classifiers.Content");
        Set<Class<?>> classifierCache = reflections.getTypesAnnotatedWith(ContentPattern.class);

        for (Class<?> clazz : classifierCache) {
            final Constructor<?> ctor = clazz.getConstructors()[0];
            try {
                classifierList.add(ContentClassifier.class.cast(ctor.newInstance(
                        new Object[]{clazz.getAnnotation(ContentPattern.class).filePath()})));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);
        String title = post.getTitle();
        String postText = post.getPostText();
        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);
        ArrayList<Boolean> booleanList = new ArrayList<Boolean>();
        try {

            for (ContentClassifier classifier : classifierList) {
                booleanList.add(classifier.classify(postText, title));
            }

            boolean ccClassify = booleanList.get(0);
            boolean cfClassify = booleanList.get(1);
            boolean daClassify = booleanList.get(2);
            boolean dbClassify = booleanList.get(3);
            boolean ecClassify = booleanList.get(4);
            boolean eoClassify = booleanList.get(5);
            boolean pkClassify = booleanList.get(6);

            contentModel.setPassedCC(ccClassify);
            contentModel.setPassedCF(cfClassify);
            contentModel.setPassedDA(daClassify);
            contentModel.setPassedDB(dbClassify);
            contentModel.setPassedEC(ecClassify);
            contentModel.setPassedEO(eoClassify);
            contentModel.setPassedPK(pkClassify);

        } catch (java.lang.StackOverflowError e) {
            contentModel.setPassedCC(false);
            contentModel.setPassedCF(false);
            contentModel.setPassedDA(false);
            contentModel.setPassedDB(false);
            contentModel.setPassedEC(false);
            contentModel.setPassedEO(false);
            contentModel.setPassedPK(false);
        }

        post.setContentClassifierPassed();
        collector.emit(tuple, new Values(post));
        collector.ack(tuple);

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
