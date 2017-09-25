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
@ContentPattern(patternName = "DB", filePath = "./src/main/resources/DB.model")
public class DBClassifier extends ContentClassifier {
    private Pattern symbalPattern;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private Pattern relatedPattern1;
    private Pattern relatedPattern2;
    private Pattern relatedPattern3;
    private Pattern relatedPattern4;
    private Pattern relatedPattern5;


    public DBClassifier(String model, String name) {
        super(model, name);
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("0|null|blank");
        unigramList.add("insert|update|create");
        unigramList.add("INTO");
        unigramList.add("table|schema|database|db");
        unigramList.add("PostgreSQL|mysql|mssql|oracle db|db2");
        unigramList.add("Database:|table:|data base:|DB Detection:");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("database dump");
        bigramList.add("db leak");
        bigramList.add("Dumped from|dumped by");
        bigramList.add("CREATE TABLE|ALTER TABLE");
        bigramList.add("INSERT INTO");
        bigramList.add("\\) values");
        bigramList.add("Found :");
        bigramList.add("Data found");
        bigramList.add("NOT NULL");
        bigramList.add("INTO users");
        bigramList.add("database dump|database dumped|db dumped|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump");
        bigramList.add("available databases");
        bigramList.add("db dump");



        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
            unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        symbalPattern = Pattern.compile("\\-|\\+|\\|");

        relatedPattern1 = getCorrectPatten("\\b" + "SQL Injection|SQLi|SQL-i|Blind SQL-i" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = getCorrectPatten("\\b" + "PRIMARY KEY|ALTER TABLE|TABLE FOUND" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern3 = Pattern.compile( "sqlmap" , Pattern.CASE_INSENSITIVE);
        relatedPattern4 = Pattern.compile( "SQL Injection|SQLi|SQL-i|Blind SQL-i|database dump|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump" , Pattern.CASE_INSENSITIVE);
        relatedPattern5 = getCorrectPatten("\\b" + "\\[\\*\\]" + "\\b", Pattern.CASE_INSENSITIVE);

    }

    @Override
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

        Matcher matcherDB = symbalPattern.matcher(text);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern2.matcher(text);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern3.matcher(title);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern4.matcher(title);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern5.matcher(text);
        feature_list += getMatchingCount(matcherDB) + ",";


        feature_list += "?";
        return headingDB + feature_list;
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

            RandomForest tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/DB.model");
            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
            String classLabel = unlabeled.classAttribute().value((int) pred);

            if("DB".equals(classLabel)){
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
        return 2;
    }
}

