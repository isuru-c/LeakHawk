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
import util.LeakHawkConstant;
import weka.classifiers.misc.SerializedClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "DNS Attack", filePath = "DA.model")
//@ContentPattern(patternName = "DNS Attack", filePath = "DA.model")
public class DAClassifier extends ContentClassifier {

    private ArrayList<String> unigrams;
    private ArrayList<String> bigrams;
    private ArrayList<String> trigrams;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private ArrayList<Pattern> trigramPatternList;

    private Pattern relatedPattern1;
    private Pattern relatedPattern3;
    private Pattern relatedPattern4;
    private Pattern relatedPattern5;
    private SerializedClassifier tclassifier;

    private String headingDA = "@relation train\n" +
            "\n" +
            "@attribute $DA1 numeric\n" +
            "@attribute $DA2 numeric\n" +
            "@attribute $DA3 numeric\n" +
            "@attribute $DA4 numeric\n" +
            "@attribute $DA5 numeric\n" +
            "@attribute $DA6 numeric\n" +
            "@attribute $DA7 numeric\n" +
            "@attribute $DA8 numeric\n" +
            "@attribute $DA9 numeric\n" +
            "@attribute $DA10 numeric\n" +
            "@attribute $DA11 numeric\n" +
            "@attribute $DA12 numeric\n" +
            "@attribute $DA13 numeric\n" +
            "@attribute $DA14 numeric\n" +
            "@attribute $DA15 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";


    public DAClassifier(String model, String name) {
        super(model, name);

        try {
            tclassifier = new SerializedClassifier();
            tclassifier.setModelFile(new File(LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH + "/" + model));
//            tclassifier = (RandomForest) weka.core.SerializationHelper.read("/home/neo/Desktop/MyFYP/Project/LeakHawk2.0/LeakHawk/leakhawk-core/src/main/resources/DA.model");
        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("DA.model file loading error.", e);
        }

        unigrams = new ArrayList<String>();
        unigrams.add(" mx | ns | ptr | cname | soa ");
        unigrams.add("dns|record|host");
        unigrams.add("snoop|axfr|brute|poisoning");

        bigrams = new ArrayList<String>();
        bigrams.add("43200 in|10800 in|86400 in|3600 in");
        bigrams.add("in a|in mx|in ns|in cname");
        bigrams.add("no ptr");
        bigrams.add("hostnames found");
        bigrams.add("zone transfer");
        bigrams.add("mx 10|mx 20|mx 30|mx 40|mx 50|mx 60");

        trigrams = new ArrayList<String>();
        trigrams.add("transfer not allowed");
        trigrams.add("Trying zone transfer");

        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigrams) {
            unigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigrams) {
            bigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigrams) {
            trigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        relatedPattern1 = Pattern.compile("dns-brute|dnsrecon|fierce|tsunami|dnsdict6|axfr", Pattern.CASE_INSENSITIVE);
        relatedPattern3 = Pattern.compile("dns leaked|dns fuck3d|zone transfer|dns|enumeration_attack", Pattern.CASE_INSENSITIVE);
        relatedPattern4 = Pattern.compile("dns_enumeration", Pattern.CASE_INSENSITIVE);
        relatedPattern5 = Pattern.compile("dns enumeration attack|dns enumeration|misconfigured dns|dns cache snooping", Pattern.CASE_INSENSITIVE);

    }

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

        for (Pattern pattern : trigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        Matcher matcher = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";


        matcher = relatedPattern3.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern4.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern5.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        feature_list += "?";
        return headingDA + feature_list;
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

            if ("pos".equals(classLabel)) {
                return true;
            }

        } catch (IOException e) {
            throw new LeakHawkDataStreamException("Post text error occured.", e);
        } catch (StackOverflowError e) {

        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("DA.model classification error.", e);
        }
        return false;
    }

    public int getSensivityLevel(String post) {
        ArrayList<String> DAlist = new ArrayList<String>(Arrays.asList("lanka", "lk", "ceylon", "sinhala", "buddhist", "colombo", "kandy", "kurunegala", "gampaha", "mahinda", "sirisena", "ranil"));
        int domainCount = 0;
        int count = 0;
        for (String i : DAlist) {
            if (post.contains(i)) {
                count++;
            }
        }
        if (domainCount < 10) {
            return 2;
        } else if (domainCount >= 10) {
            return 3;
        }
        return 1;
    }


}
