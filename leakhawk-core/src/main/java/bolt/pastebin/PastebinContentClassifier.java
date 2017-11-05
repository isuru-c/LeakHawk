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

package bolt.pastebin;

import bolt.core.LeakHawkClassifier;
import classifier.Content.*;
import exception.LeakHawkClassifierLoadingException;
import exception.LeakHawkDataStreamException;
import model.ContentData;
import model.ContentModel;
import model.Post;
import org.reflections.Reflections;
import util.LeakHawkConstant;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This Bolt is for classify the content into the different sensitive classes
 *
 * @author Isuru Chandima
 * @author Sugeesh Chandraweera
 * @author Warunika Amali
 */
@SuppressWarnings("ALL")
public class PastebinContentClassifier extends LeakHawkClassifier {

    /**
     * This classifierList will contain the custom classifier list load on the run time
     */
    private ArrayList<ContentClassifier> classifierList;

    @Override
    public void prepareClassifier() {
        classifierList = new ArrayList<>();

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
                throw new LeakHawkClassifierLoadingException("Content Classifier loading failed.", e);
            } catch (IllegalAccessException e) {
                throw new LeakHawkClassifierLoadingException("Content Classifier loading failed.", e);
            } catch (InvocationTargetException e) {
                throw new LeakHawkClassifierLoadingException("Content Classifier loading failed.", e);
            }
        }
    }

    @Override
    protected String getBoltName() {
        return LeakHawkConstant.PASTEBIN_CONTENT_CLASSIFIER;
    }

    @Override
    public void classifyPost(Post post) {
        //TODO  set url content classification
        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);

        String title = post.getTitle();
        String postText = post.getPostText();
        List<ContentData> contentDataList = new ArrayList();

        /* Check post with each classifier and if it is match add the classifier type and
           sensitivity to the contentDataList */
        for (ContentClassifier classifier : classifierList) {
            try {
                if (classifier.classify(postText, title)) {
                    ContentData contentData = new ContentData(classifier.getName(), classifier.getSensivityLevel(postText));
                    contentDataList.add(contentData);
                    contentModel.setContentFound(true);
                }else if(post.getUrlList().size()>0){
                    if(classifier.classify(post.getUrlContent(),title)) {
                        ContentData contentData = new ContentData(classifier.getName(), classifier.getSensivityLevel(postText));
                        contentDataList.add(contentData);
                        contentModel.setContentFound(true);
                    }
                }
            } catch (java.lang.StackOverflowError e) {
               throw new LeakHawkDataStreamException("Stackoverflow Error- too big file");
            }
        }
        contentModel.setContentDataList(contentDataList);
        if (contentModel.isContentFound()) {
            increaseOutCount();
        }
        post.setNextOutputStream(LeakHawkConstant.P_CONTENT_CLASSIFIER_TO_SYNTHESIZER);

    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkConstant.P_CONTENT_CLASSIFIER_TO_SYNTHESIZER);

        return outputStream;
    }
}
