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

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "Configuration files", filePath = "./src/main/resources/CF.model")
//@ContentPattern(patternName = "Configuration files", filePath = "CF.model")
public class CFClassifier extends ContentClassifier{

    private Pattern cfSymbalPattern;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private ArrayList<Pattern> trigramPatternList;

    private Pattern digitPattern;
    private Pattern alphaPattern;
    private Pattern alphDigitPattern;
    private RandomForest tclassifier;
    private String headingCF = "@relation CF\n" +
            "\n" +
            "@attribute $CF1 numeric\n" +
            "@attribute $CF2 numeric\n" +
            "@attribute $CF3 numeric\n" +
            "@attribute $CF4 numeric\n" +
            "@attribute $CF5 numeric\n" +
            "@attribute $CF6 numeric\n" +
            "@attribute $CF7 numeric\n" +
            "@attribute $CF8 numeric\n" +
            "@attribute $CF9 numeric\n" +
            "@attribute $CF10 numeric\n" +
            "@attribute $CF11 numeric\n" +
            "@attribute $CF12 numeric\n" +
            "@attribute $CF13 numeric\n" +
            "@attribute $CF14 numeric\n" +
            "@attribute $CF15 numeric\n" +
            "@attribute $CF16 numeric\n" +
            "@attribute $CF17 numeric\n" +
            "@attribute $CF18 numeric\n" +
            "@attribute $CF19 numeric\n" +
            "@attribute $CF20 numeric\n" +
            "@attribute $CF21 numeric\n" +
            "@attribute $CF22 numeric\n" +
            "@attribute $CF23 numeric\n" +
            "@attribute $CF24 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";

    public CFClassifier(String model, String name) {
        super(model,name);
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("ip");
        unigramList.add("cisco");
        unigramList.add("password-encryption");
        unigramList.add("spanning-tree");
        unigramList.add("domain-lookup");
        unigramList.add("serial0/0/0");
        unigramList.add("access-list");


        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("interface FastEthernet[0-9]|interface Serial[0-9]");
        bigramList.add("speed auto|duplex auto");
        bigramList.add("0 line");
        bigramList.add("line vty|line aux|line con\"");
        bigramList.add("service password");
        bigramList.add("ip address");
        bigramList.add("ip route");
        bigramList.add("banner motd");
        bigramList.add("no service");
        bigramList.add("clock rate");
        bigramList.add("ip cef|ipv6 cef");
        bigramList.add("service password-encryption");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("line vty 0|line con 0|line aux 0");
        trigramList.add("no ip address");
        trigramList.add("no ipv6 cef");
        trigramList.add("switchport access vlan");


        cfSymbalPattern = Pattern.compile("!");

        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
                unigramPatternList.add(getCorrectPatten(word, Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(getCorrectPatten(word, Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            trigramPatternList.add(getCorrectPatten(word, Pattern.CASE_INSENSITIVE));
        }
    }

    public String createARFF(String text,String title) {
        String feature_list = "";

        for (Pattern pattern : unigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : bigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : trigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        Matcher matcherCF = cfSymbalPattern.matcher(text);
        feature_list += getMatchingCount(matcherCF) + ",";

        feature_list += "?";
        return headingCF+feature_list;
    }

    @Override
    public boolean classify(String text,String title) {
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

            String classLabel = unlabeled.classAttribute().value((int) pred);

            if("CF".equals(classLabel)){
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
        if (post.contains("enable password")) {
            return 3;
        }
        return 1;
    }
}

