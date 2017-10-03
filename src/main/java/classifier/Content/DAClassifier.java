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
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "DNS Attack", filePath = "./src/main/resources/DA.model")
//@ContentPattern(patternName = "DNS Attack", filePath = "DA.model")
public class DAClassifier extends ContentClassifier {

    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private ArrayList<Pattern> trigramPatternList;

    private Pattern relatedPattern1;
    private Pattern relatedPattern2;
    private Pattern relatedPattern3;
    private Pattern relatedPattern4;
    private Pattern relatedPattern5;
    private Pattern relatedPattern6;
    private Pattern relatedPattern7;
    private RandomForest tclassifier;

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
            "@attribute $DA16 numeric\n" +
            "@attribute $DA17 numeric\n" +
            "@attribute $DA18 numeric\n" +
            "@attribute $DA19 numeric\n" +
            "@attribute @@class@@ {DA,non}\n" +
            "\n" +
            "@data\n";


    public DAClassifier(String model, String name) {
        super(model, name);

        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(model);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("MX|NS|PTR|CNAME|SOA");
        unigramList.add("dns|record|host");
        unigramList.add("INTO");
        unigramList.add("snoop|axfr|brute|poisoning");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("43200 IN|10800 IN|86400 IN|3600 IN");
        bigramList.add("IN A|IN MX|IN NS|IN CNAME");
        bigramList.add("no PTR");
        bigramList.add("hostnames found");
        bigramList.add("zone transfer");
        bigramList.add("MX 10|MX 20|MX 30|MX 40|MX 50|MX 60");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("transfer not allowed");
        trigramList.add("Trying zone transfer");


        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
                unigramPatternList.add(getCorrectPatten(word, Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            if(word.equals("MX 10|MX 20|MX 30|MX 40|MX 50|MX 60")) {
                bigramPatternList.add(getCorrectPatten(word, Pattern.CASE_INSENSITIVE));
            }else {
                bigramPatternList.add(getCorrectPatten(word));
            }
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            if(word.equals("Trying zone transfer")) {
                trigramPatternList.add(getCorrectPatten(word, Pattern.CASE_INSENSITIVE));
            }else {
                trigramPatternList.add(getCorrectPatten(word));
            }

        }

        relatedPattern1 = getCorrectPatten("\\b" + "dns-brute|dnsrecon|fierce|tsunami|Dnsdict6|axfr" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = Pattern.compile("dns-brute|dnsrecon|fierce|tsunami|Dnsdict6|axfr", Pattern.CASE_INSENSITIVE);
        relatedPattern3 = getCorrectPatten("\\b" + "DNS LeAkEd|DNS fuck3d|zone transfer|DNS_Enumeration|Enumeration_Attack" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern4 = Pattern.compile("DNS LeAkEd|DNS fuck3d|zone transfer|DNS_Enumeration|Enumeration_Attack", Pattern.CASE_INSENSITIVE);
        relatedPattern5 = getCorrectPatten("\\b" + "DNS Enumeration Attack|DNS enumeration|zone transfer|misconfigured DNS|DNS Cache Snooping" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern6 = Pattern.compile("DNS Enumeration Attack|DNS enumeration|zone transfer|misconfigured DNS|DNS Cache Snooping", Pattern.CASE_INSENSITIVE);
        relatedPattern7 = Pattern.compile("\\b" +"\\[\\*\\]"+"\\b", Pattern.CASE_INSENSITIVE);
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

        Matcher matcher = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern2.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern3.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern4.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern5.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern6.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern7.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        feature_list += "?";
        return headingDA+feature_list;
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

            if("DA".equals(classLabel)){
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getSensivityLevel(String post){
        ArrayList<String> DAlist = new ArrayList<String>(Arrays.asList("lanka", "lk", "ceylon", "sinhala", "buddhist", "colombo", "kandy", "kurunegala", "gampaha", "mahinda", "sirisena", "ranil"));
        int domainCount = 0;
        int count=0;
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
