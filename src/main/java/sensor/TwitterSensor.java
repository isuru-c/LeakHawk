/*
 *    Copyright 2017 SWIS
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

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * This get the data from twitter api and send them to the kafka
 *
 * @author Isuru Chandima
 */
public class TwitterSensor extends Thread{

    private String consumerKey = "";
    private String consumerSecret = "";
    private String token = "";
    private String tokenSecret = "";
    private LeakHawkKafkaProducer leakHawkKafkaProducer;
    private Producer<String, String> pastebinProducer;

    public TwitterSensor(){
        consumerKey = "Qrk3fZ04WaW0Qw0zVE7MSwYNi";
        consumerSecret = "9jXaU9kTDHh2pLGDyQc69AI9YhHmj2Huf2AbYcaWKgE8M3Jmzy";
        token = "1627974024-AmWhRjy2pThPIpc1nwEhTmhws1U0AYPHkukUZrc";
        tokenSecret = "HC7Vq3VSsOLuQ1QjZ3NihpwCymWi00pbvT10kelCtS29t";
        leakHawkKafkaProducer = new LeakHawkKafkaProducer();
        pastebinProducer = leakHawkKafkaProducer.getProducer();
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public void run(){
        // Create tweeter connection
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(100);
        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("twitterapi");
        arrayList.add("a");
        endpoint.trackTerms(arrayList);
        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);

        ClientBuilder builder = new ClientBuilder()
                .hosts(hosts)
                .authentication(auth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(queue));

        Client client = builder.build();
        client.connect();

        String topic = "tweets";

        for (int msgRead = 0; true; msgRead++) {
            ProducerRecord<String, String> message = null;
            try {
                message = new ProducerRecord<String, String>(topic, queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pastebinProducer.send(message);
        }
//        pastebinProducer.close();
//        client.stop();
    }
}
