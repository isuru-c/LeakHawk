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

import data.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Isuru Chandima on 7/3/17.
 */
public class ContextFilterBolt extends BaseRichBolt {

    public Properties properties = new Properties();
    public List<String> regExpHandlerList;
    OutputCollector collector;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    public void execute(Tuple tuple) {

        Post post = (Post)tuple.getValue(0);

        //if context filter is passed forward the data to next bolt(Evidence classifier)
        if (isPassContextFilter(post.getPostText())) {
            //pass to evidence classifier
            collector.emit(tuple, new Values(post));
            System.out.println("\nUser: " + post.getUser() + "\nTitle: " + post.getTitle() + "\n" + post.getPostText() + "\n--- Filtered in by context filter ---\n");
        } else {
            //System.out.println("\nUser: " + post.getUser() + "\nTitle: " + post.getTitle() + "\n" + post.getPostText() + "\n--- Filtered out by context filter ---\n");
        }

        //collector.emit(tuple, new Values(post));

        collector.ack(tuple);

    }


    public boolean isPassContextFilter(String entry) {
        InputStream input = null;
        try {
            input = new FileInputStream(new File("./src/main/resources/context.properties"));
            // load a properties file
            properties.load(input);
            loadRegExpList(18);
            if (regExpressionMatched(entry) == true) {
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
//            System.out.println(regExpHandlerList);

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
                if (found == true) {
                    break;
                }
            }
            if (found == true)
                return true;
        } catch (Exception ex) {

        }
        return false;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
