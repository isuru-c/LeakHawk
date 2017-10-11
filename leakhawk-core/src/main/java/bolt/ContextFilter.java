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

import bolt.core.LeakHawkFilter;
import exception.LeakHawkFilePathException;
import model.Post;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.*;
import net.didion.jwnl.dictionary.Dictionary;

import util.LeakHawkParameters;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Bolt is to filter in the domain related posts
 *
 * @author Isuru Chandima
 * @author Warunika Amali
 * @author Sugeesh Chandraweera
 */
public class ContextFilter extends LeakHawkFilter {

    private List<String> regularExpressionList;
    private ArrayList<String> synonyms;

    @Override
    public void prepareFilter() {
        this.createRegularExpressionList();
        this.createSynonyms();
    }

    @Override
    protected String getBoltName() {
        return LeakHawkParameters.CONTEXT_FILTER;
    }

    private void createRegularExpressionList() {
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("context.properties");
            Properties properties = new Properties();
            properties.load(input);
            regularExpressionList = new ArrayList<>();
            for (int i = 0; i < properties.size(); i++) {
                regularExpressionList.add(properties.getProperty("regexp" + (i + 1)));
            }
        } catch (IOException e) {
            throw new LeakHawkFilePathException("Can't load context.properties file.", e);
        }
    }

    private void createSynonyms() {
        synonyms = new ArrayList<>();
        ArrayList<String> wordSet = new ArrayList<>();
        try {
            InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("SriLanka.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                wordSet.add(strLine);
            }
            br.close();

            // initialize JWNL (this must be done before JWNL can be used)
            JWNL.initialize(this.getClass().getClassLoader().getResourceAsStream("properties.xml"));
        } catch (IOException e) {
            throw new LeakHawkFilePathException("Can't load SriLanka.txt file.", e);
        } catch (JWNLException e) {
            throw new LeakHawkFilePathException("Can't load properties.xml file.", e);
        }
        Dictionary dictionary = Dictionary.getInstance();

        // Create the list of synonyms using given list of words
        for (String word : wordSet) {
            try {
                synonyms.add(word.toLowerCase());
                IndexWord connectedNouns = dictionary.lookupIndexWord(POS.NOUN, word);
                IndexWord connectedVerbs = dictionary.lookupIndexWord(POS.VERB, word);
                matchSynonyms(connectedNouns);
                matchSynonyms(connectedVerbs);
            } catch (JWNLException e) {
                throw new LeakHawkFilePathException("Properties.xml file Config Error.", e);
            }
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
                throw new LeakHawkFilePathException("Wordnet Syset generation error", e);
            }
        }
    }


    @Override
    public boolean isFilterPassed(Post post) {
        String postText = post.getPostText();

        if (isContextFilterPassed(postText)) {

            increaseOutCount();

            if (post.getPostType().equals(LeakHawkParameters.POST_TYPE_PASTEBIN)) {
                post.setNextOutputStream(LeakHawkParameters.CONTEXT_FILTER_TO_P_EVIDENCE_CLASSIFIER);
            } else if (post.getPostType().equals(LeakHawkParameters.POST_TYPE_TWEETS)) {
                post.setNextOutputStream(LeakHawkParameters.CONTEXT_FILTER_TO_T_EVIDENCE_CLASSIFIER);
            }
            return true;
        }

        return false;
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
        for (String stringPattern : regularExpressionList) {
            Pattern pattern = Pattern.compile(String.valueOf(stringPattern));
            Matcher matcher = pattern.matcher(postText);
            if (matcher.find()) {
                found = true;
                break;
            }
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

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkParameters.CONTEXT_FILTER_TO_P_EVIDENCE_CLASSIFIER);
        outputStream.add(LeakHawkParameters.CONTEXT_FILTER_TO_T_EVIDENCE_CLASSIFIER);

        return outputStream;
    }
}
