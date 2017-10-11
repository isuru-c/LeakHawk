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

package bolt.twitter;

import bolt.core.LeakHawkFilter;
import model.Post;
import util.LeakHawkParameters;

import java.io.*;
import java.util.ArrayList;

/**
 * This Bolt is used to filter out posts that does not contain any sensitive data like
 * game, movies, torrents and porn contents in tweets.
 * Extends the superclass LeakHawkPreFilter
 *
 * @author Isuru Chandima
 */
public class TwitterPreFilter extends LeakHawkFilter {

    private ArrayList<String> keywordList;

    @Override
    public void prepareFilter() {

        try {
            InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("TwitterPreFilterList.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String strLine;
            keywordList = new ArrayList<>();

            while ((strLine = bufferedReader.readLine()) != null) {
                keywordList.add(strLine);
            }

            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getBoltName() {
        return LeakHawkParameters.TWEETS_PRE_FILTER;
    }

    @Override
    public boolean isFilterPassed(Post post) {
        // Convert the tweet to the lower case
        String postText = post.getPostText().toLowerCase();
        post.setPostText(postText);

        // Drop re-tweets, non English posts and filter in only tweets that does not contain given keywords
        try {
            if (postText.substring(0, 4).equals("rt @")) {
                // Drop this retweet, no further operations
                return false;
            } else if (!post.getLanguage().equals("en")) {
                // Language is not English, drop the tweet
                return false;
            } else if (!isContainKeyword(post.getPostText())) {
                // Filter in for the context filter
                post.setNextOutputStream(LeakHawkParameters.T_PRE_FILTER_TO_CONTEXT_FILTER);
                return true;
            }
        } catch (NullPointerException e) {
            // This is a temporary solution to handle twitter control tweets
            // needs to solve correctly
            return false;
        }
        return false;
    }

    /**
     * Check the twitter post with the pre defined list of keywords and if the tweet
     * contains any of the keyword, then return as true. Those positive tweets will be
     * filter out from the LeakHawk engine.
     *
     * @param postText twitter post needs ot check for availability of a keyword
     * @return true if the post contains any given words in keywordList, false otherwise
     */
    private boolean isContainKeyword(String postText) {

        for (String keyword : keywordList) {
            if (postText.contains(keyword))
                return true;
        }

        return false;
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkParameters.T_PRE_FILTER_TO_CONTEXT_FILTER);

        return outputStream;
    }
}
