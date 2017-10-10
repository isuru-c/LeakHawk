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

package bolt.twitter;

import bolt.core.LeakHawkClassifier;
import model.ContentModel;
import model.Post;
import util.LeakHawkParameters;

import java.util.ArrayList;

/**
 * This class is used to classify tweets into different sensitive classes
 *
 * @author Isuru Chandima
 */
public class TweetContentClassifier extends LeakHawkClassifier {

    @Override
    public void prepareClassifier() {

    }

    @Override
    public void classifyPost(Post post) {

        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);

        post.setNextOutputStream(LeakHawkParameters.T_CONTENT_CLASSIFIER_TO_SYNTHESIZER);
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkParameters.T_CONTENT_CLASSIFIER_TO_SYNTHESIZER);

        return outputStream;
    }

}
