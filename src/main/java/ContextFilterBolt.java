import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sun.org.apache.xerces.internal.utils.SecuritySupport.getResourceAsStream;


public class ContextFilterBolt extends BaseRichBolt {
    OutputCollector collector;
    public static Properties properties = new Properties();
    public static List<String> regExpHandlerList;
    public static String entry = "bank of ceylon hacked";
//   / public ArrayList<String> entryList=new ArrayList<>(Arrays.asList("bank of ceylon","hacked", "anonymous"));


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

        collector.emit(tuple, new Values(type, key, date, user, title, syntax, post));

        collector.ack(tuple);
        System.out.println("*******************Context Filter********************************");
        if (isPassContextFilter() == true) {
            //pass to evidence classifier
            System.out.println("Passed context filter");
        }
    }


    public static boolean isPassContextFilter() {
        InputStream input = null;
        try {
            input = getResourceAsStream("context.properties");
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

    public static void loadRegExpList(int rgexpCount) {
        regExpHandlerList = new ArrayList<String>();
        for (int i = 1; i <= rgexpCount; i++) {
            regExpHandlerList.add(properties.getProperty("regexp" + i));
//            System.out.println(regExpHandlerList);

        }
    }


    private static boolean regExpressionMatched(String input) {

//        System.out.println(input);
        boolean found = false;

        try {
            for (String pattern : regExpHandlerList) {
                Pattern p = Pattern.compile(String.valueOf(pattern));
//                System.out.println(pattern);
                Matcher m = p.matcher(input);
                if (m.find()) {
//                    System.out.println(pattern);
                    System.out.println("found");
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
