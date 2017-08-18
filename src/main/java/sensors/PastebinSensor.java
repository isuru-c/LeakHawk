package sensors;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Properties;

/**
 * Created by Isuru Chandima on 6/19/17.
 */
public class PastebinSensor extends Thread {

    public PastebinSensor() {

    }

    public void run() {

        // Create Kafka producer

        Properties properties = new Properties();
        //Assign localhost id
        properties.put("bootstrap.servers", "localhost:9092");
        //Set acknowledgements for producer requests
        properties.put("acks", "all");
        //If the request fails, the producer can automatically retry,
        properties.put("retries", 0);
        //Specify buffer size in config
        properties.put("batch.size", 16384);
        //Reduce the no of requests less than 0
        properties.put("linger.ms", 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);

        String topic = "pastebin-posts";
        ProducerRecord<String, String> message = null;

        try {
            int postLimit = 100;
            URL my_url = new URL("http://pastebin.com/api_scraping.php?limit=" + postLimit);
            String lastKey = "";
            boolean pastebinSensorRunning = true;
            while (pastebinSensorRunning) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(my_url.openStream()));
                StringBuilder webPageContent = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    webPageContent.append(line);
                }
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(webPageContent.toString());
                JSONArray array = (JSONArray) obj;
                boolean lastKeyFound = false;
                for (int i = array.size() - 1; i >= 0; i--) {
                    String post = array.get(i).toString();
                    JSONObject postDetails = (JSONObject) parser.parse(post);
                    String key = (String) postDetails.get("key");
                    if (lastKeyFound) {
                        lastKey = key;
                    } else if (lastKey == "" || lastKey.equals(key)) {
                        if (lastKey == "") i++;
                        lastKey = key;
                        lastKeyFound = true;
                        continue;
                    } else {
                        //System.out.println("Last key: " + lastKey + "\tCurrent key: " + key);
                        continue;
                    }
                    //System.out.println("Current key: " + key);
                    message = new ProducerRecord<String, String>(topic, post);
                    producer.send(message);
                }
                sleep(10000);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producer.close();

    }

}
