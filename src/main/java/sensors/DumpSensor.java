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

package sensors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by Isuru Chandima on 9/11/17.
 */
public class DumpSensor extends Thread{

    private String[] posts = {"post 1", "post 2"};

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

        String topic = "dump-posts";
        ProducerRecord<String, String> message = null;


        for(String post: posts) {
            message = new ProducerRecord<String, String>(topic, post);
            producer.send(message);
        }

        producer.close();

    }

}
