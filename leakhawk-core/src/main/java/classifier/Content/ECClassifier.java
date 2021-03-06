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
@ContentPattern(patternName = "Email conversation", filePath = "EC.model")
//@ContentPattern(patternName = "Email conversation", filePath = "EC.model")
public class ECClassifier extends ContentClassifier {
    private ArrayList<String> unigrams;
    private Pattern titlePattern;
    private ArrayList<Pattern> unigramPatternList;
    private Pattern relatedPattern1;
    private SerializedClassifier tclassifier;

    private String headingEC = "@relation train\n" +
            "\n" +
            "@attribute $EC1 numeric\n" +
            "@attribute $EC2 numeric\n" +
            "@attribute $EC3 numeric\n" +
            "@attribute $EC4 numeric\n" +
            "@attribute $EC5 numeric\n" +
            "@attribute $EC6 numeric\n" +
            "@attribute $EC7 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";


    public ECClassifier(String model, String name) {
        super(model, name);

        try {
            tclassifier = new SerializedClassifier();
            tclassifier.setModelFile(new File(LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH + "/" + model));
//            tclassifier = (RandomForest) weka.core.SerializationHelper.read("/home/neo/Desktop/MyFYP/Project/LeakHawk2.0/LeakHawk/leakhawk-core/src/main/resources/EC.model");
        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("EC.model file loading error.", e);
        }
        unigrams = new ArrayList<String>();
        unigrams.add("reply|forward");
        unigrams.add("subject");
        unigrams.add("mailed by");
        unigrams.add("from:|wrote:|to:|subject:");
        unigrams.add("regards|dear");


        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigrams) {
            unigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        titlePattern = Pattern.compile("conversation|email", Pattern.CASE_INSENSITIVE);
        relatedPattern1 = Pattern.compile(">");


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

            if ("pos".equals(classLabel)) {
                return true;
            }

        } catch (IOException e) {
            throw new LeakHawkDataStreamException("Post text error occured.", e);
        } catch (StackOverflowError e) {

        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("EC.model classification error.", e);
        }
        return false;
    }

    public int getSensivityLevel(String post) {
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

