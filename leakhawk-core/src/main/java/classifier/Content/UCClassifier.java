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

import util.LeakHawkConstant;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Warunika Amali
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "User Credentials", filePath = "UC.model")
public class UCClassifier extends ContentClassifier{

    private int hashCount=0;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private Pattern titlePattern;
    private Pattern relatedTerms2Pattern;
    private Pattern relatedTerms3Pattern;
    private Pattern ucPattern;
    private RandomForest tclassifier;
    private String headingCC = "@relation UC\n" +
            "\n" +
            "@attribute $UC1 numeric\n" +
            "@attribute $UC2 numeric\n" +
            "@attribute $UC3 numeric\n" +
            "@attribute $UC4 numeric\n" +
            "@attribute $UC5 numeric\n" +
            "@attribute $UC6 numeric\n" +
            "@attribute $UC7 numeric\n" +
            "@attribute $UC8 numeric\n" +
            "@attribute $UC9 numeric\n" +
            "@attribute $UC10 numeric\n" +
            "@attribute $UC11 numeric\n" +
            "@attribute $UC12 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";

    public UCClassifier(String model, String name) {
        super(model, name);
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH+"/"+model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("password|passw0rd|passwd|passwort");
        unigramList.add("user|username|admin");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("user name");
        bigramList.add("password dump");
        bigramList.add("Dumped from|dumped by");
        bigramList.add("login dump");
        bigramList.add("credential dump");

        ucPattern = Pattern.compile("(([a-zA-Z]|[0-9])|([-]|[_]|[.]))+[@](([a-zA-Z0-9])|([-])){2,63}([.]((([a-zA-Z0-9])|([-])){2,63})){1,4}");

        unigramPatternList = new ArrayList<>();
        for (String word : unigramList) {
            unigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        bigramPatternList = new ArrayList<>();
        for (String word : bigramList) {
            bigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        titlePattern = Pattern.compile("password|passwords|passw0rd|passwd|passwort|username|user name|credentials|password dump|Dumped from|dumped by|login dump|credentials dump|password dumped by", Pattern.CASE_INSENSITIVE);
        relatedTerms2Pattern = Pattern.compile("password|passw0rd|passwd|passwords|passwort|username|user name|credentials|password dump|Dumped from|dumped by|login dump|credentials dump|password dumped by|email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack", Pattern.CASE_INSENSITIVE);
        relatedTerms3Pattern = Pattern.compile("facebook|username|user name|login|email|e_mail|emailID|email_address|credentials|Dumped from|dumped by|login dump|credentials dump|password dumped by|id|email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack", Pattern.CASE_INSENSITIVE);

    }

    /*
    Creating the arff file of new post
     */
    public String createARFF(String text, String title) {

        String feature_list = "";

        for (Pattern pattern : unigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : bigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        Matcher matcherCC = ucPattern.matcher(text);
        feature_list += getMatchingCount(matcherCC) + ",";

        Matcher matcher = titlePattern.matcher(title);
        int matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";

        matcher = relatedTerms2Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";

        matcher = relatedTerms3Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";

        extractHashCount(text);
        feature_list +=hashCount + ",";
        feature_list += "?";

        return headingCC + feature_list;
    }


    @Override
    public boolean classify(String text, String title) {
        try{
            // convert String into InputStream
            String result = createARFF(text,title);
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
            labeled.instance(0).setClassValue(pred);

            String classLabel = unlabeled.classAttribute().value((int) pred);
            if("pos".equals(classLabel)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getSensivityLevel(String post) {

        if ((hashCount < 5 && hashCount > 0)) {
            return 1;
        } else if ((hashCount < 20) && (hashCount > 5)) {
            return 2;
        } else if (hashCount > 20) {
            return 3;
        }
        return 0;
    }

    /*
    Getting the hash count in the post
     */
    public void extractHashCount(String post) {

        Hash_id hash_id = new Hash_id();

        String[] words= post.split(" |\n");
        for(String word:words){
            if(hash_id.isHash(word)==true) hashCount++;
        }
    }
}
