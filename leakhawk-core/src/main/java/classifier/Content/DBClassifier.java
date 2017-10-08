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
@ContentPattern(patternName = "Database", filePath = "./src/main/resources/DB.model")
//@ContentPattern(patternName = "Database", filePath = "DB.model")
public class DBClassifier extends ContentClassifier {

    private ArrayList<String> unigrams;
    private ArrayList<String> bigrams;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private Pattern relatedPattern1;
    private Pattern relatedPattern2;
    private Pattern relatedPattern3;
    private Pattern relatedPattern4;
    private RandomForest tclassifier;

    private String headingDB = "@relation DB\n" +
            "\n" +
            "@attribute $DB1 numeric\n" +
            "@attribute $DB2 numeric\n" +
            "@attribute $DB3 numeric\n" +
            "@attribute $DB4 numeric\n" +
            "@attribute $DB5 numeric\n" +
            "@attribute $DB6 numeric\n" +
            "@attribute $DB7 numeric\n" +
            "@attribute $DB8 numeric\n" +
            "@attribute $DB9 numeric\n" +
            "@attribute $DB10 numeric\n" +
            "@attribute $DB11 numeric\n" +
            "@attribute $DB12 numeric\n" +
            "@attribute $DB13 numeric\n" +
            "@attribute $DB14 numeric\n" +
            "@attribute $DB15 numeric\n" +
            "@attribute $DB16 numeric\n" +
            "@attribute $DB17 numeric\n" +
            "@attribute $DB18 numeric\n" +
            "@attribute $DB19 numeric\n" +
            "@attribute $DB20 numeric\n" +
            "@attribute $DB21 numeric\n" +
            "@attribute $DB22 numeric\n" +
            "@attribute $DB23 numeric\n" +
            "@attribute $DB24 numeric\n" +
            "@attribute $DB25 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";



    public DBClassifier(String model, String name) {
        super(model, name);

        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(this.getClass().getClassLoader().getResourceAsStream("DB.model"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        unigrams = new ArrayList<String>();
        unigrams.add("database");
        unigrams.add("table");
        unigrams.add("schema|database|db");
        unigrams.add("postgresql|mysql|mssql|oracle db|db2|MySQL");
        unigrams.add("dbms|database|Database:|table:|data base:|db detection:|data bases: ");

        bigrams = new ArrayList<String>();
        bigrams.add("database dump|db user :|db Version :");
        bigrams.add("update table|create table");
        bigrams.add("hacked database");
        bigrams.add("leaked database | database leaked");
        bigrams.add("db leak");
        bigrams.add("dumped from|dumped by");
        bigrams.add("create table|alter table");
        bigrams.add("insert into");
        bigrams.add("\\) values");
        bigrams.add("found :");
        bigrams.add("data found");
        bigrams.add("not null");
        bigrams.add("into users");
        bigrams.add("database dump|database dumped|db dumped|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump");
        bigrams.add("available databases");
        bigrams.add("db dump");

        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigrams) {
            unigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigrams) {
            bigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        relatedPattern1 = Pattern.compile("sql injection|sqli|sql-i|blind sql-i", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = Pattern.compile("primary key|alter table|table found", Pattern.CASE_INSENSITIVE);
        relatedPattern3 = Pattern.compile( "sqlmap" , Pattern.CASE_INSENSITIVE);
        relatedPattern4 = Pattern.compile( "sql injection|sqli|sql-i|blind sql-i|database dump|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump" , Pattern.CASE_INSENSITIVE);

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

        Matcher matcherDB = null;

        matcherDB = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern2.matcher(text);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern3.matcher(title);
        feature_list += getMatchingCount(matcherDB) + ",";

        matcherDB = relatedPattern4.matcher(title);
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

