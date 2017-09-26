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

package classifier.Content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Content Classifier is the template for the custom classifiers. Custom classifiers can follow this template and easily
 * add the classifier to the system.
 *
 * @author Sugeesh Chandraweera
 */

public abstract class ContentClassifier {

    /**
     * This field is for identifying the content classifier
     */
    String name = null;

    /**
     * This field is for storing the path of the model file. (model file should be .arff  file)
     */
    String model = null;


    public ContentClassifier(String model, String name) {
        this.model = model;
        this.name = name;
    }

    /**
     * This method will classify the post sensitive in that type or not
     * @param text text content of the post
     * @param title title of the post
     * @return boolean value for the classification
     */
    public abstract boolean classify(String text, String title);


    /**
     * This method will return the matching count for a given matcher
     * @param matcher matcher Object
     * @return num of matching count
     */
    int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    /**
     * This method will return a Pattern object for given  word and type
     * @param word word
     * @param type type
     * @return Pattern Object
     */
    Pattern getCorrectPatten(String word, int type) {
        Pattern compile = Pattern.compile(word.replaceAll("\\|", "\b|\b"), type);
        return compile;
    }

    /**
     * This method will return a Pattern object for given word
     * @param word word
     * @return Pattern Object
     */
    Pattern getCorrectPatten(String word) {
        Pattern compile = Pattern.compile(word.replaceAll("\\|", "\b|\b"));
        return compile;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract int getSensivityLevel(String post);
}
