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

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class ContextFilter extends BaseRichBolt {

    private Properties properties = new Properties();
    private List<String> regExpHandlerList;
    private OutputCollector collector;
    private ArrayList<String> synonyms = new ArrayList<String>();
    private static ArrayList<String> wordset = new ArrayList<String>(Arrays.asList("Sri Lanka", "bank", "Sinhala", "South Asia", "Mathripala sirisena",
            "Mahinda Rajapaksa", "Ranil Wickramasinghe", "Chandrika Kumaratunga", "Sarath Fonseka", "Gotabhaya Rajapaksa", "Shavendra Silva",
            "Velupillai Prabhakaran", "Vinayagamoorthy Muralitharan", "Karuna Amman", "Cargills", "keels", "aitken spence", "hemas", "LTTE",
            "Colombo", "Kandy", "Kurunegala", "Gampaha"));
    Dictionary dictionary;

    private String postTypePastebin = "pastebin-posts";
    private String postTypeTweets = "tweets";

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        configureWordNet();
        dictionary = Dictionary.getInstance();
    }

    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);
        //if context filter is passed forward the data to next bolt(Evidence classifier)
        if(post.getPostType() == postTypePastebin) {
            if (isPassContextFilter(post.getPostText())) {
                collector.emit("pastebin-out", tuple, new Values(post));
            }
        }else if (post.getPostType().equals(postTypeTweets)){
            collector.emit("tweets-out", tuple, new Values(post));
        }
        collector.ack(tuple);

    }

    public boolean isPassContextFilter(String entry) {
        InputStream input = null;

        try {
            input = new FileInputStream(new File("./src/main/resources/context.properties"));
            properties.load(input);
            loadRegExpList(18);
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

    public void loadRegExpList(int rgexpCount) {
        regExpHandlerList = new ArrayList<String>();
        for (int i = 1; i <= rgexpCount; i++) {
            regExpHandlerList.add(properties.getProperty("regexp" + i));
            //System.out.println(regExpHandlerList);
        }
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

    public boolean isWordsetMatched(String entry) {
        for (String s : wordset) {
            getSynonyms(s);
        }
        for (String s : synonyms) {
            if (entry.toUpperCase().contains(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public void getSynonyms(String noun) {
        IndexWord indexWord1 = null;
        IndexWord indexWord2 = null;
        synonyms.add(noun);
        try {
            indexWord1 = dictionary.lookupIndexWord(POS.NOUN, noun);
            indexWord2 = dictionary.lookupIndexWord(POS.VERB, noun);
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        try {
            if (indexWord1 != null) {
                for (Synset synset : indexWord1.getSenses()) {
                    Word[] words = synset.getWords();
                    for (Word word : words) {
                        if (!synonyms.contains(word.getLemma())) {
//                            System.out.println("\t"+word.getLemma());
                            synonyms.add(word.getLemma());
                        }
                    }
                }
            }
            if (indexWord2 != null) {
                for (Synset synset : indexWord2.getSenses()) {
                    Word[] words = synset.getWords();
                    for (Word word : words) {
                        if (!synonyms.contains(word.getLemma())) {
//                            System.out.println("\t"+word.getLemma());
                            synonyms.add(word.getLemma());
                        }
                    }
                }
            }


        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    public static void configureWordNet() {
        try {
            // initialize JWNL (this must be done before JWNL can be used)
            // See the JWordnet documentation for details on the properties file
            JWNL.initialize(new FileInputStream("./src/main/resources/properties.xml"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        outputFieldsDeclarer.declareStream("pastebin-out", new Fields("post"));
        outputFieldsDeclarer.declareStream("tweets-out", new Fields("post"));
    }
}