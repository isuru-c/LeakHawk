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

package leakhawk_core.sensor;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;


/**
 * Kafka producer is required for each Sensor to put new posts to the kafka broker
 * This class can be used to create new kafka producer for each sensor.
 *
 * @author Isuru Chandima
 */
public class LeakHawkKafkaProducer {

    private Properties properties;

    public LeakHawkKafkaProducer(){

        properties = new Properties();
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

    }

    public Producer<String, String> getProducer(){
        return new KafkaProducer<String, String>(properties);
    }
}
