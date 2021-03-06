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
import bolt.pastebin.PastebinPreFilter;
import model.Post;
import util.LeakHawkConstant;
import weka.core.Stopwords;

import java.io.*;
import java.util.ArrayList;

import static util.LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH;


/**
 * This Bolt is used to filter out posts that does not contain any sensitive data like
 * game, movies, torrents and porn contents in tweets.
 * Extends the superclass LeakHawkPreFilter
 *
 * @author Isuru Chandima
 * @author Udeshika Sewwandi
 */
public class TwitterPreFilter extends LeakHawkFilter {

    private ArrayList<String> keywordList;

    @Override
    public void prepareFilter() {

        try {

//          InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("TwitterPreFilterList.txt");
            InputStream fileInputStream = new FileInputStream(RESOURCE_FOLDER_FILE_PATH+"/PreFilter_twitter.txt");
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
        return LeakHawkConstant.TWEETS_PRE_FILTER;
    }

    @Override
    public boolean isFilterPassed(Post post) {
        // Convert the tweet to the lower case
        String postText = post.getPostText().toLowerCase();
        post.setPostText(postText);

        // If the tweet is possibly sensitive
        if(post.isPossiblySensitive()){
            post.setNextOutputStream(LeakHawkConstant.T_PRE_FILTER_TO_CONTEXT_FILTER);
            return true;
        }

        // Drop re-tweets, non English posts and filter in only tweets that does not contain given keywords
        try {
            if (postText.length()>4 && "rt @".equals(postText.substring(0, 4))) {
                // Drop this retweet, no further operations
                return false;
            } else if (!"en".equals(post.getLanguage())) {
                // Language is not English, drop the tweet
                return false;
            } else if (!isContainKeyword(post.getPostText())) {
                // Filter in for the context filter
                increaseOutCount();
                post.setNextOutputStream(LeakHawkConstant.T_PRE_FILTER_TO_CONTEXT_FILTER);
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
        /*get stop words removed text*/
        String preprocessedText = stopWordsRemoval(postText);

        for (String keyword : keywordList) {
            if (preprocessedText.contains(keyword))
                return true;
        }

        return false;
    }

    /**
     * Returns the post by removing stop words related to English language
     * @param post
     * @return stop words removed post
     */
    public String stopWordsRemoval(String post){
        Stopwords stpWord = new Stopwords();

        /*remove words from stop word list which may appear in program codes*/
        stpWord.remove("and");
        stpWord.remove("as");
        stpWord.remove("do");
        stpWord.remove("for");
        stpWord.remove("i");
        stpWord.remove("if");
        stpWord.remove("is");
        stpWord.remove("in");
        stpWord.remove("not");
        stpWord.remove("of");
        stpWord.remove("on");
        stpWord.remove("or");
        stpWord.remove("this");

        StringBuilder stringBuilder = new StringBuilder(post);
        /*split string from spaces*/
        String[] words = stringBuilder.toString().split("\\s");
        String fullString = "";

        /*replace stop words with a space*/
        for(int i=0;i<words.length;i++){
            if(stpWord.is(words[i])){
                words[i] = "";
            }
        }

        /*concatenate string without stop words*/
        for(String word:words){
            fullString+=word+" ";
        }
        return fullString;
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkConstant.T_PRE_FILTER_TO_CONTEXT_FILTER);

        return outputStream;
    }
}
