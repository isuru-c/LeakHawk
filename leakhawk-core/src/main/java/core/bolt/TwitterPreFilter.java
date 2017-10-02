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

package core.bolt;

import core.model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import core.parameters.LeakHawkParameters;

import java.util.ArrayList;
import java.util.Map;

/**
 * This Bolt is used to filter out posts that does not contain any sensitive data like
 * game, movies, torrents and porn contents
 *
 * @author Isuru Chandima
 */
public class TwitterPreFilter extends BaseRichBolt {

    private OutputCollector collector;
    private ArrayList<String> keywordList;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        keywordList = LeakHawkParameters.getTwitterPreFilterKeywordList();
    }

    @Override
    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);

        // Convert the tweet to the lower case
        String postText = post.getPostText().toLowerCase();
        post.setPostText(postText);

        // Drop re-tweets, non English posts and filter in only tweets that does not contain given keywords
        if (postText.substring(0, 4).equals("rt @")) {
            // Drop this retweet, no further operations
        } else if (!post.getLanguage().equals("en")) {
            // Language is not English, drop the tweet
        } else if (!isContainKeyword(post.getPostText())) {
            // Filter in for the context filter
            collector.emit(tuple, new Values(post));
        } else{
            // Tweets contain the given keywords, drop them
        }

        collector.ack(tuple);
    }

    /**
     * Check the twitter post with the pre defined list of keywords and if the tweet
     * contains any of the keyword, then return as true. Those positive tweets will be
     * filter out from the LeakHawk engine.
     *
     * @param postText twitter post needs ot check for availability of a keyword
     * @return
     */
    public boolean isContainKeyword(String postText) {

        for (String keyword : keywordList) {
            if (postText.contains(keyword))
                return true;
        }

        return false;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
