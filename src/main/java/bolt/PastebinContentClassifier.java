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

package bolt;

import classifier.Content.*;
import model.ContentData;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This Bolt is for classify the content into the different sensitive classes
 *
 * @author Isuru Chandima
 * @author Sugeesh Chandraweera
 */
public class PastebinContentClassifier extends BaseRichBolt {

    private OutputCollector collector;

    /**
     * This classifierList will contain the custom classifier list load on the run time
     */
    private ArrayList<ContentClassifier> classifierList;


    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        classifierList = new ArrayList<ContentClassifier>();

        // Load the all the ContentPatterns
        Reflections reflections = new Reflections("classifier.Content");
        Set<Class<?>> classifierCache = reflections.getTypesAnnotatedWith(ContentPattern.class);

        // Create Objects from every ContentPattern
        for (Class<?> clazz : classifierCache) {
            final Constructor<?> ctor = clazz.getConstructors()[0];
            try {
                classifierList.add(ContentClassifier.class.cast(ctor.newInstance(
                        new Object[]{clazz.getAnnotation(ContentPattern.class).filePath(),
                                clazz.getAnnotation(ContentPattern.class).patternName()})));
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
        List<ContentData> contentDataList = new ArrayList();
        contentModel.setContentDataList(contentDataList);

        try {
            /* Check post with each classifier and if it is match add the classifier type and
            sensitivity to the contentDataList */
            for (ContentClassifier classifier : classifierList) {
                if(classifier.classify(postText, title)) {
                    ContentData contentData = new ContentData(classifier.getName(), classifier.getSensivityLevel(postText));
                    contentDataList.add(contentData);
                }
            }
        } catch (java.lang.StackOverflowError e) {
            /* If message is too long */
            //TODO read only the first 20 lines
            System.out.println("\n\n\n\n");
            e.printStackTrace();
        }

        post.setContentClassifierPassed();
        post.setContentModel(contentModel);
        collector.emit(tuple, new Values(post));
        collector.ack(tuple);

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
