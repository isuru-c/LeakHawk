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

import model.Post;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.*;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.FileInputStream;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
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
public class ContextFilter extends BaseRichBolt {

    private Properties properties = new Properties();
    private List<String> regExpHandlerList;
    private OutputCollector collector;
    private ArrayList<String> synonyms = new ArrayList<String>();
    private static ArrayList<String> wordset = new ArrayList<String>();
    Dictionary dictionary;
    private int rgexpCount=18;

    private String pastebinOut = "pastebin-out";
    private String tweetsOut = "tweets-out";

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        try {
            configureWordNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dictionary = Dictionary.getInstance();
    }

    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);

        if(post.getPostType().equals(LeakHawkParameters.postTypePastebin)) {
            if (isPassContextFilter(post.getPostText())) {
                collector.emit(pastebinOut, tuple, new Values(post));
            }
        }else if (post.getPostType().equals(LeakHawkParameters.postTypeTweets)){
            collector.emit(tweetsOut, tuple, new Values(post));
        }

        collector.ack(tuple);
    }

    private boolean isPassContextFilter(String entry) {
        InputStream input = null;

        try {
            input = new FileInputStream(new File("./src/main/resources/context.properties"));
            properties.load(input);
            regExpHandlerList = new ArrayList<String>();
            for (int i = 1; i <= rgexpCount; i++) {
                regExpHandlerList.add(properties.getProperty("regexp" + i));
                //System.out.println(regExpHandlerList);
            }
            if (regExpressionMatched(entry) || isWordsetMatched(entry)) {
                return true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean regExpressionMatched(String input) {
        boolean found = false;
        try {
            for (String pattern : regExpHandlerList) {
                Pattern p = Pattern.compile(String.valueOf(pattern));
                Matcher m = p.matcher(input);
                if (m.find()) {
                    found = true;
                }
                if (found) {
                    break;
                }
            }
            if (found)
                return true;
        } catch (Exception ex) {

        }
        return false;
    }

    private boolean isWordsetMatched(String entry) {
        for (String s : wordset) {
            IndexWord indexWord1 = null;
            IndexWord indexWord2 = null;
            synonyms.add(s);
            try {
                indexWord1 = dictionary.lookupIndexWord(POS.NOUN, s);
                indexWord2 = dictionary.lookupIndexWord(POS.VERB, s);
            } catch (JWNLException e) {
                e.printStackTrace();
            }
            matchSynonyms(indexWord1);
            matchSynonyms(indexWord2);
        }
        for (String s : synonyms) {
            if (entry.toUpperCase().contains(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private void matchSynonyms(IndexWord indexWord){
        if (indexWord != null) {
            try {
                for (Synset synset : indexWord.getSenses()) {
                    Word[] words = synset.getWords();
                    for (Word word : words) {
                        if (!synonyms.contains(word.getLemma())) {
                            synonyms.add(word.getLemma());
                        }
                    }
                }
            } catch (JWNLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void configureWordNet() throws IOException {
        try {
            // initialize JWNL (this must be done before JWNL can be used)
            // See the JWordnet documentation for details on the properties file
            JWNL.initialize(new FileInputStream("./src/main/resources/properties.xml"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        FileInputStream fstream = new FileInputStream("./src/main/resources/SriLanka.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        while ((strLine = br.readLine()) != null)   {
            wordset.add(strLine);
        }
        br.close();
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(pastebinOut, new Fields("post"));
        outputFieldsDeclarer.declareStream(tweetsOut, new Fields("post"));
    }
}
