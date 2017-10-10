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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "Private keys", filePath = "./src/main/resources/PK.model")
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
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";


    public PKClassifier(String model, String name) {
        super(model, name);
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(this.getClass().getClassLoader().getResourceAsStream("PK.model"));
        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("PK.model file loading error.", e);
        }
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("private");
        unigramList.add("key");
        unigramList.add("rsa");
        unigramList.add("sshrsa");
        unigramList.add("key-----");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("private key");
        bigramList.add("rsa private");
        bigramList.add("begin certificate");
        bigramList.add("dsa private");
        bigramList.add("encrypted private");
        bigramList.add("begin rsa");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("rsa private key");
        trigramList.add("dsa private key");
        trigramList.add("begin rsa private");
        trigramList.add("end private key");
        trigramList.add("begin private key");

        ArrayList<String> fourgramList = new ArrayList<String>();
        fourgramList.add("begin rsa private key");
        fourgramList.add("begin dsa private key");
        fourgramList.add("end rsa private key");

        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
            unigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            trigramPatternList.add(Pattern.compile( word, Pattern.CASE_INSENSITIVE));
        }

        fourgramPatternList = new ArrayList<Pattern>();
        for (String word : fourgramList) {
            fourgramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        relatedPattern1 = Pattern.compile("[-]{5}[A-Za-z0-9 ]+[-]{5}");
        relatedPattern2 = Pattern.compile("public_key|private_key|private key|rsa private|dsa private|encrypted private|begin rsa|rsa private key|dsa private key|begin rsa private|end private key|begin private key", Pattern.CASE_INSENSITIVE);

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

            if("pos".equals(classLabel)){
                return true;
            }

        } catch (IOException e) {
            throw new LeakHawkDataStreamException("Post text error occured.", e);
        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("PK.model classification error.", e);
        }
        return false;
    }

    @Override
    public int getSensivityLevel(String post){
        return 3;
    }

}
