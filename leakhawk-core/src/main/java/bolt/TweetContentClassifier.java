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

import bolt.core.LeakHawkContentClassifier;
import model.ContentModel;
import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 *
 * This class is used to classify tweets into different sensitive classes
 *
 * @author Isuru Chandima
 */
public class TweetContentClassifier extends LeakHawkContentClassifier {

    @Override
    public void prepareContentClassifier() {

    }

    @Override
    public void executeContentClassifier(Post post, ContentModel contentModel, Tuple tuple, OutputCollector collector) {
        collector.emit(tuple, new Values(post));
    }
}