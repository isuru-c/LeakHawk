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


public class ContextFilterBolt extends BaseRichBolt {
    OutputCollector collector;
    public Properties properties = new Properties();
    public List<String> regExpHandlerList;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    public void execute(Tuple tuple) {
        String type = tuple.getString(0);
        String key = tuple.getString(1);
        String date = tuple.getString(2);
        String user = tuple.getString(3);
        String title = tuple.getString(4);
        String syntax = tuple.getString(5);
        String post = tuple.getString(6);

        System.out.println("*******************Context Filter********************************");

        //if context filter is passed forward the data to next bolt(Evidence classifier)
        if (isPassContextFilter(post) == true) {
            //pass to evidence classifier
            System.out.println("Passed context filter: " + post);
            collector.emit(tuple, new Values(type, key, date, user, title, syntax, post));
        }
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

//        System.out.println(input);
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
            if (found == true) return true;


        } catch (Exception ex) {

        }


        return false;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("type", "key", "date", "user", "title", "syntax", "post"));
    }
}
