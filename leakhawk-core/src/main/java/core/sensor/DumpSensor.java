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

package core.sensor;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import core.parameters.LeakHawkParameters;

import java.io.*;


/**
 * This Sensor is used for testing purposes. It can be used to check the
 * functionality of the LeakHawk by using dump posts without getting real time
 * posts from pastebin or social media.
 * <p>
 * It reads files from the post folder in the root folder of the project and fed
 * posts in those files as real posts to the LeakHawk so all other functionality
 * can be tested. Any post can be put as text file in the post folder.
 *
 * @author Isuru Chandima
 */
public class DumpSensor extends Thread {

    private LeakHawkKafkaProducer leakHawkKafkaProducer;
    private Producer<String, String> dumpProducer;

    public DumpSensor() {
        leakHawkKafkaProducer = new LeakHawkKafkaProducer();
        dumpProducer = leakHawkKafkaProducer.getProducer();
    }

    public void run() {

        try {

            // Get the list of files from the dump post folder
            File folder = new File(LeakHawkParameters.dumpFolderPath);
            File[] listOfFiles = folder.listFiles();

            // Read the each file and feed the content of the file as a post to the LeakHawk
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

                        ProducerRecord<String, String> message = new ProducerRecord<String, String>(LeakHawkParameters.postTypeDump, post);
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
