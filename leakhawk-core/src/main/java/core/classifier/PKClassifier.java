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

package core.classifier;

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
@ContentPattern(patternName = "Private keys", filePath = "./leakhawk-core/src/main/resources/PK.model")
//@ContentPattern(patternName = "Private keys", filePath = "PK.model")
public class PKClassifier extends ContentClassifier {

    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private ArrayList<Pattern> trigramPatternList;
    private ArrayList<Pattern> fourgramPatternList;

    private Pattern relatedPattern1;
    private Pattern relatedPattern2;

    private RandomForest tclassifier;

    private String headingPK = "@relation PK\n" +
            "\n" +
            "@attribute $PK1 numeric\n" +
            "@attribute $PK2 numeric\n" +
            "@attribute $PK3 numeric\n" +
            "@attribute $PK4 numeric\n" +
            "@attribute $PK5 numeric\n" +
            "@attribute $PK6 numeric\n" +
            "@attribute $PK7 numeric\n" +
            "@attribute $PK8 numeric\n" +
            "@attribute $PK9 numeric\n" +
            "@attribute $PK10 numeric\n" +
            "@attribute $PK11 numeric\n" +
            "@attribute $PK12 numeric\n" +
            "@attribute $PK13 numeric\n" +
            "@attribute $PK14 numeric\n" +
            "@attribute $PK15 numeric\n" +
            "@attribute $PK16 numeric\n" +
            "@attribute $PK17 numeric\n" +
            "@attribute $PK18 numeric\n" +
            "@attribute $PK19 numeric\n" +
            "@attribute $PK20 numeric\n" +
            "@attribute $PK21 numeric\n" +
            "@attribute @@class@@ {PK,non}\n" +
            "\n" +
            "@data\n";


    public PKClassifier(String model, String name) {
        super(model, name);
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("PRIVATE");
        unigramList.add("KEY");
        unigramList.add("RSA");
        unigramList.add("SSHRSA");
        unigramList.add("KEY-----");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("PRIVATE KEY");
        bigramList.add("RSA PRIVATE");
        bigramList.add("BEGIN CERTIFICATE");
        bigramList.add("DSA PRIVATE");
        bigramList.add("ENCRYPTED PRIVATE");
        bigramList.add("BEGIN RSA");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("RSA PRIVATE KEY");
        trigramList.add("DSA PRIVATE KEY");
        trigramList.add("BEGIN RSA PRIVATE");
        trigramList.add("END PRIVATE KEY");
        trigramList.add("BEGIN PRIVATE KEY");

        ArrayList<String> fourgramList = new ArrayList<String>();
        trigramList.add("BEGIN RSA PRIVATE KEY");
        trigramList.add("BEGIN DSA PRIVATE KEY");
        trigramList.add("END RSA PRIVATE KEY");


        /*
        *

	PK20=$(grep -oE "[-]{5}[A-Za-z0-9 ]+[-]{5}" "$i"| wc -l);

	#PK related terms
	PK21=$(grep -owiE "PRIVATE KEY|RSA PRIVATE|DSA PRIVATE|ENCRYPTED PRIVATE|BEGIN RSA|RSA PRIVATE KEY|DSA PRIVATE KEY|BEGIN RSA PRIVATE|END PRIVATE KEY|BEGIN PRIVATE KEY" "$i"| wc -l);

        * */



        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
                unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            trigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        fourgramPatternList = new ArrayList<Pattern>();
        for (String word : fourgramList) {
            fourgramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        relatedPattern1 = Pattern.compile("[-]{5}[A-Za-z0-9 ]+[-]{5}");
        relatedPattern2 = getCorrectPatten("\\b"+"PRIVATE KEY|RSA PRIVATE|DSA PRIVATE|ENCRYPTED PRIVATE|BEGIN RSA|RSA PRIVATE KEY|DSA PRIVATE KEY|BEGIN RSA PRIVATE|END PRIVATE KEY|BEGIN PRIVATE KEY"+"\\b", Pattern.CASE_INSENSITIVE);
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

        for (Pattern pattern : fourgramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        Matcher matcher = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern2.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        feature_list += "?";
        return headingPK + feature_list;
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

            if("PK".equals(classLabel)){
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
    public int getSensivityLevel(String post){
        return 3;
    }

}