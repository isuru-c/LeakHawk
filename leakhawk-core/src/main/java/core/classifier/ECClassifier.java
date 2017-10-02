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
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "Email conversation", filePath = "./leakhawk-core/src/main/resources/EC.model")
//@ContentPattern(patternName = "Email conversation", filePath = "EC.model")
public class ECClassifier extends ContentClassifier {
    private Pattern titlePattern;
    private ArrayList<Pattern> unigramPatternList;

    private Pattern relatedPattern1;
    private Pattern relatedPattern2;
    private Pattern relatedPattern3;
    private Pattern relatedPattern4;
    private Pattern relatedPattern5;
    private Pattern relatedPattern6;
    private Pattern relatedPattern7;
    private RandomForest tclassifier;

    private String headingEC = "@relation train\n" +
            "\n" +
            "@attribute $EC1 numeric\n" +
            "@attribute $EC2 numeric\n" +
            "@attribute $EC3 numeric\n" +
            "@attribute $EC4 numeric\n" +
            "@attribute $EC5 numeric\n" +
            "@attribute $EC6 numeric\n" +
            "@attribute $EC7 numeric\n" +
            "@attribute @@class@@ {EC,non}\n" +
            "\n" +
            "@data\n";

    public ECClassifier(String model, String name) {
        super(model, name);

        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(model);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("reply|forward");
        unigramList.add("subject");
        unigramList.add("mailed by");
        unigramList.add("from:|wrote:|to:|subject:");
        unigramList.add("regards");


        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
            unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        titlePattern = Pattern.compile("conversation|email", Pattern.CASE_INSENSITIVE);
        relatedPattern1 = Pattern.compile( ">" );


    }

    public String createARFF(String text, String title) {
        String feature_list = "";

        for (Pattern pattern : unigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }


        Matcher matcherEC = titlePattern.matcher(title);
        feature_list += getMatchingCount(matcherEC) + ",";

        matcherEC = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherEC) + ",";

        feature_list += "?";
        return headingEC + feature_list;
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

            if("EC".equals(classLabel)){
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
        ArrayList<String> ECList = new ArrayList<String>(Arrays.asList("CONFIDENTIAL", "secret", "do not disclose"));
        int ecCount = 0;
        for (String i : ECList) {
            if (post.contains(i)) {
                ecCount++;
            }
        }
        if (ecCount > 0) {
            return 3;
        }
        return 1;
    }

}

