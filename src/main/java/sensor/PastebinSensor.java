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

package sensor;

import org.apache.kafka.clients.producer.Producer;
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


/**
 * This Sensor will put the pastebin files getting from the pastebin api to the kafka
 *
 * @author Isuru Chandima
 */
public class PastebinSensor extends Thread {

    private LeakHawkKafkaProducer leakHawkKafkaProducer;
    private Producer<String, String> pastebinProducer;
    private String topic = "pastebin-posts";

    public PastebinSensor() {
        leakHawkKafkaProducer = new LeakHawkKafkaProducer();
        pastebinProducer = leakHawkKafkaProducer.getProducer();
    }

    public void run() {

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
                    pastebinProducer.send(message);
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
        pastebinProducer.close();

    }

}
