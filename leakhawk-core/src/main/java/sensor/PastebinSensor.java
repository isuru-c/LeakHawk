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

import exception.LeakHawkDataStreamException;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.LeakHawkConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * This Sensor will be connected to the pastebin API and fetch posts in every
 * LeakHawkConstant.pastebinSensorSleepTime. In each request
 * LeakHawkConstant.pastebinPostLimit number of latest posts will be fetched.
 * The sensor required to find only the new posts which were not in previous
 * reply and feed those posts to the kafka broker (LeakHawk)
 *
 * In pastebin scraping API only the list of keys of the pastebin post are given.
 * LeakHawk needs aditional mechanisam to fetch each individual post later
 *
 * It is essential to whitelist the IP of the machine which runiing pastebin client
 * before starting to fetch posts. Otherwise it will not work.
 *
 * For that https://pastebin.com/api_scaping_faq URL can be used
 *
 * @author Isuru Chandima
 */
public class PastebinSensor extends Thread {

    // Kafka producer to feed tweets into kafka broke
    private Producer<String, String> pastebinProducer;

    private volatile boolean pastebinSensorRunning = true;

    /**
     * Set kafka producer for pastebin sensor
     */
    public PastebinSensor() {
        LeakHawkKafkaProducer leakHawkKafkaProducer = new LeakHawkKafkaProducer();
        pastebinProducer = leakHawkKafkaProducer.getProducer();
    }

    public void run() {
        ProducerRecord<String, String> message = null;
        try {
            URL my_url = new URL(LeakHawkConstant.PASTEBIN_SCRAPING_URL + LeakHawkConstant.PASTEBIN_POST_LIMIT);
            String lastKey = "";
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
                        continue;
                    }
                    message = new ProducerRecord<String, String>(LeakHawkConstant.POST_TYPE_PASTEBIN, post);
                    pastebinProducer.send(message);
                }
                sleep(LeakHawkConstant.PASTEBIN_SENSOR_SLEEP_TIME);
            }
        } catch (MalformedURLException e) {
            throw new LeakHawkDataStreamException("Pastebin Sensor Failed.", e);
        } catch (IOException e) {
            throw new LeakHawkDataStreamException("Pastebin Sensor Failed.", e);
        } catch (ParseException e) {
            throw new LeakHawkDataStreamException("Pastebin Sensor Failed.", e);
        } catch (InterruptedException e) {
            throw new LeakHawkDataStreamException("Pastebin Sensor Failed.", e);
        }
        pastebinProducer.close();
    }


    public boolean stopSensor(){
        this.pastebinSensorRunning = false;
        return true;
    }

}
