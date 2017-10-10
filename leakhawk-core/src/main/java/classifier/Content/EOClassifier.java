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

package classifier.Content;

import exception.LeakHawkClassifierLoadingException;
import exception.LeakHawkDataStreamException;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "Email only", filePath = "./src/main/resources/EO.model")
//@ContentPattern(patternName = "Email only", filePath = "EO.model")
public class EOClassifier extends ContentClassifier {

    private Pattern relatedPattern1;
    private Pattern relatedPattern2;
    private Pattern emailPattern;
    private RandomForest tclassifier;

    private String headingEO = "@relation train\n" +
            "\n" +
            "@attribute $EO1 numeric\n" +
            "@attribute $EO2 numeric\n" +
            "@attribute $EO3 numeric\n" +
            "@attribute $EO4 numeric\n" +
            "@attribute $EO5 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";

    public EOClassifier(String model, String name) {
        super(model, name);
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(this.getClass().getClassLoader().getResourceAsStream("EO.model"));
        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("EO.model file loading error.", e);
        }
        relatedPattern1 = Pattern.compile("email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = Pattern.compile("leaked by|emails leaked|domains hacked|leaked email list|email list leaked|leaked emails|leak of|email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack", Pattern.CASE_INSENSITIVE);
        emailPattern = Pattern.compile("(([a-zA-Z]|[0-9])|([-]|[_]|[.]))+[@](([a-zA-Z0-9])|([-])){2,63}([.]((([a-zA-Z0-9])|([-])){2,63})){1,4}");
    }


    public String createARFF(String text, String title) {
        String feature_list = "";


        Matcher matcherEO = relatedPattern1.matcher(title);
        feature_list += getMatchingCount(matcherEO) + ",";

        matcherEO = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherEO) + ",";


        matcherEO = emailPattern.matcher(text);
        int emailCount = getMatchingCount(matcherEO);
        feature_list += emailCount + ",";

        int wordCount= text.replace('[', ' ').replace('*', ' ').replace(']', ' ').replace(',', ' ').replace('/', ' ').replace(':', ' ').split("\\s+").length;
        feature_list += wordCount + ",";

        double rate = ((double) emailCount/wordCount)*100;
        if(rate>89){
            feature_list += 1 + ",";
        }else {
            feature_list += 0 + ",";
        }

        feature_list += "?";
        return headingEO + feature_list;
    }

    @Override
    public boolean classify(String text, String title) {
        try {
            // convert String into InputStream
            String result = createARFF(text, title);
            InputStream is = new ByteArrayInputStream(result.getBytes());

            // read it with BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // BufferedReader reader = new BufferedReader
            Instances unlabeled = new Instances(reader);
            reader.close();
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // create copy
            Instances labeled = new Instances(unlabeled);

            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
            String classLabel = unlabeled.classAttribute().value((int) pred);

            if("pos".equals(classLabel)){
                return true;
            }

        }catch (IOException e) {
            throw new LeakHawkDataStreamException("Post text error occured.", e);
        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("EO.model classification error.", e);
        }
        return false;
    }

    public int getSensivityLevel(String post){
        int email_count = EOCounter(post);
        if (email_count < 50) {
            return 1;
        }
        return 2;
    }

    public int EOCounter(String post) {
        Pattern emailPattern = Pattern.compile("(([a-zA-Z]|[0-9])|([-]|[_]|[.]))+[@](([a-zA-Z0-9])|([-])){2,63}([.]((([a-zA-Z0-9])|([-])){2,63})){1,4}");
        Matcher matcherEO = emailPattern.matcher(post);
        int EO_Count = getMatchingCount(matcherEO);
        return EO_Count;
    }
}

