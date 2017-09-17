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

import java.io.*;
import java.util.Properties;

/**
 * Created by Isuru Chandima on 9/11/17.
 */
public class DumpSensor extends Thread {

    private LeakHawkKafkaProducer leakHawkKafkaProducer;
    private Producer<String, String> dumpProducer;
    private String topic = "dump-posts";

    public DumpSensor() {

        leakHawkKafkaProducer = new LeakHawkKafkaProducer();
        dumpProducer = leakHawkKafkaProducer.getProducer();
    }

    public void run() {

        ProducerRecord<String, String> message = null;

        try {
            File folder = new File("./posts");
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                try {
                    if (file.isFile()) {
                        BufferedReader br = new BufferedReader(new FileReader(file));

                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null) {
                            sb.append(line + "\n");
                            line = br.readLine();
                        }

                        String post = sb.toString();

                        message = new ProducerRecord<String, String>(topic, post);
                        dumpProducer.send(message);

                        br.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        dumpProducer.close();
    }

}
