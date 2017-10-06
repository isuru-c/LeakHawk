/*
 *     Copyright 2017 SWIS
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

import bolt.core.LeakHawkContextFilter;
import model.Post;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.*;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.FileInputStream;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import parameters.LeakHawkParameters;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Bolt is to filter in the domain related posts
 *
 * @author Isuru Chandima
 * @author Warunika Amali
 */
public class ContextFilter extends LeakHawkContextFilter {

    /**
     * These identifiers are defined to identify output streams from context filter
     * to the evidence classifiers
     */
    private String pastebinOut = "context-filter-pastebin-out";
    private String tweetsOut = "context-filter-tweets-out";

    private List<String> regularExpressionList;
    private ArrayList<String> synonyms;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);

        createRegularExpressionList();
        createSynonyms();

    }

    @Override
    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);

        String postText = post.getPostText();

        if (isContextFilterPassed(postText)) {
            if (post.getPostType().equals(LeakHawkParameters.postTypePastebin)) {
                collector.emit(pastebinOut, tuple, new Values(post));
            } else if (post.getPostType().equals(LeakHawkParameters.postTypeTweets)) {
                collector.emit(tweetsOut, tuple, new Values(post));
            } else if (post.getPostType().equals(LeakHawkParameters.postTypeDump)) {
                // Dump posts are emit as pastebin posts
                collector.emit(pastebinOut, tuple, new Values(post));
            }
        }

        collector.ack(tuple);
    }

    /**
     * Checking whether context filter is passed
     *
     * @param postText
     * @return
     */
    private boolean isContextFilterPassed(String postText) {

        return isRegularExpressionMatched(postText) || isSynonymsMatched(postText);
    }

    /**
     * matching the posts with regular expressions
     *
     * @param postText
     * @return
     */
    private boolean isRegularExpressionMatched(String postText) {

        boolean found = false;
        try {
            for (String stringPattern : regularExpressionList) {

                Pattern pattern = Pattern.compile(String.valueOf(stringPattern));
                Matcher matcher = pattern.matcher(postText);

                if (matcher.find()) {
                    found = true;
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return found;
    }

    /**
     * Checking whether the post has matching words
     *
     * @param postText
     * @return
     */
    private boolean isSynonymsMatched(String postText) {

        // Check if the postText contains synonyms generated before
        for (String synonym : synonyms) {
            if (postText.contains(synonym)) {
                return true;
            }
        }

        return false;
    }

    private void createRegularExpressionList() {

        try {
            InputStream input = new FileInputStream(new File("./src/main/resources/context.properties"));
            Properties properties = new Properties();
            properties.load(input);
            regularExpressionList = new ArrayList<>();

            for (int i = 0; i < properties.size(); i++) {
                regularExpressionList.add(properties.getProperty("regexp" + (i + 1)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSynonyms() {

        synonyms = new ArrayList<>();
        ArrayList<String> wordSet = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream("./src/main/resources/SriLanka.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));

            String strLine;

            while ((strLine = br.readLine()) != null) {
                wordSet.add(strLine);
            }
            br.close();

            // initialize JWNL (this must be done before JWNL can be used)
            // See the JWordnet documentation for details on the properties file
            JWNL.initialize(new FileInputStream("./src/main/resources/properties.xml"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Dictionary dictionary = Dictionary.getInstance();

        // Create the list of synonyms using given list of words
        for (String word : wordSet) {

            IndexWord connectedNouns = null;
            IndexWord connectedVerbs = null;

            synonyms.add(word.toLowerCase());

            try {
                connectedNouns = dictionary.lookupIndexWord(POS.NOUN, word);
                connectedVerbs = dictionary.lookupIndexWord(POS.VERB, word);
            } catch (JWNLException e) {
                e.printStackTrace();
            }

            matchSynonyms(connectedNouns);
            matchSynonyms(connectedVerbs);
        }
    }

    /**
     * Matching the synonyms from wordnet
     *
     * @param indexWord
     */
    private void matchSynonyms(IndexWord indexWord) {
        if (indexWord != null) {
            try {
                for (Synset synset : indexWord.getSenses()) {
                    Word[] words = synset.getWords();
                    for (Word word : words) {
                        if (!synonyms.contains(word.getLemma())) {
                            synonyms.add(word.getLemma().toLowerCase());
                        }
                    }
                }
            } catch (JWNLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * In the current topology, output of the context filter is connected to two different
     * bolts [evidence classifiers] depend on the type of the post. Hence two output streams
     * are defined in here.
     *
     * pastebinOut - output stream for the PastebinEvidenceClassifier
     * tweetsOut - output stream for the tweetsEvidenceClassifier
     *
     * These exact identifiers are needs to be used when creating the storm topology.
     *
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        outputFieldsDeclarer.declareStream(pastebinOut, new Fields("post"));
        outputFieldsDeclarer.declareStream(tweetsOut, new Fields("post"));
    }
}
