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
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import parameters.LeakHawkParameters;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * This sensor connects the twitter stream API to the LeakHawk
 * Given the parameters consumer key, consumer secret, token and token secret
 * it can connect to the stream API and continuously fetch tweets from the
 * twitter and feed them to the kafka broker with the topic defined as in
 * LeakHawkParameters.postTypeTweets
 *
 * @author Isuru Chandima
 */
public class TwitterSensor extends Thread {

    // Required parameters to connect to the twitter stream API
    private String consumerKey = "";
    private String consumerSecret = "";
    private String token = "";
    private String tokenSecret = "";

    // Kafka producer to feed tweets into kafka broker
    private Producer<String, String> twitterProducer;

    /**
     * Set twitter API parameters and kafka producer for twitter sensor
     */
    public TwitterSensor() {

        // Set the parameters for the twitter stream API
        consumerKey = LeakHawkParameters.consumerKey;
        consumerSecret = LeakHawkParameters.consumerSecret;
        token = LeakHawkParameters.token;
        tokenSecret = LeakHawkParameters.tokenSecret;

        LeakHawkKafkaProducer leakHawkKafkaProducer = new LeakHawkKafkaProducer();
        twitterProducer = leakHawkKafkaProducer.getProducer();
    }

    public void run() {

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
                .hosts(hosts).authentication(auth)
                .endpoint(endpoint).processor(new StringDelimitedProcessor(queue));

        Client client = builder.build();
        client.connect();

        // Fed tweets continuously and put into kafka broker
        while (true) {
            ProducerRecord<String, String> message = null;
            try {
                message = new ProducerRecord<String, String>(LeakHawkParameters.postTypeTweets, queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            twitterProducer.send(message);
        }
    }
}
