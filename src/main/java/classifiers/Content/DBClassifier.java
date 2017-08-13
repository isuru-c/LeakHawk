package classifiers.Content;

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
public class DBClassifier extends ContentClassifier {
    Pattern symbalPattern;
    ArrayList<Pattern> unigramPatternList;
    ArrayList<Pattern> bigramPatternList;
    ArrayList<Pattern> trigramPatternList;

    Pattern relatedPattern1;
    Pattern relatedPattern2;
    Pattern relatedPattern3;
    Pattern relatedPattern4;
    Pattern relatedPattern5;
    Pattern relatedPattern6;
    Pattern relatedPattern7;

    public DBClassifier() {
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
        bigramList.add(") values");
        bigramList.add("Found :");
        bigramList.add("Data found");
        bigramList.add("NOT NULL");
        bigramList.add("INTO users");
        bigramList.add("database dump|database dumped|db dumped|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump");
        bigramList.add("available databases");
        bigramList.add("db dump");




        /*
        *
        *
	#Injection related terms
	DB21=$(grep -owiE "SQL Injection|SQLi|SQL-i|Blind SQL-i" "$i"| wc -l);

	#DB related terms
	DB22=$(grep -owiE "PRIMARY KEY|ALTER TABLE|TABLE FOUND" "$i"| wc -l);

	#SQL injection tool
	DB23=$(ls "$i" | grep -oiE "sqlmap"| wc -l);

	#filename
	DB24=$(ls "$i" | grep -oiE "SQL Injection|SQLi|SQL-i|Blind SQL-i|database dump|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump" | wc -l);

	#blended features
	DB25=$(grep -owiE "\[\*\]" "$i"| wc -l);*/


        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
            unigramPatternList.add(Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        symbalPattern = Pattern.compile("\\-|\\+|\\|");

        relatedPattern1 = Pattern.compile("\\b" + "SQL Injection|SQLi|SQL-i|Blind SQL-i" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = Pattern.compile("\\b" + "PRIMARY KEY|ALTER TABLE|TABLE FOUND" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern3 = Pattern.compile( "sqlmap" , Pattern.CASE_INSENSITIVE);
        relatedPattern4 = Pattern.compile( "SQL Injection|SQLi|SQL-i|Blind SQL-i|database dump|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump" , Pattern.CASE_INSENSITIVE);
        relatedPattern5 = Pattern.compile("\\b" + "\\[\\*\\]" + "\\b", Pattern.CASE_INSENSITIVE);

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

        for (Pattern pattern : trigramPatternList) {
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


        feature_list += ",?";
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
//        System.out.println("Result:"+pred);

            if (pred >= 0.5) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

