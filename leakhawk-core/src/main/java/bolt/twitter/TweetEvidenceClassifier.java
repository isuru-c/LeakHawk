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

package bolt.twitter;

import bolt.core.LeakHawkClassifier;
import model.Post;
import model.EvidenceModel;
import db.DBConnection;
import db.DBHandle;
import util.LeakHawkConstant;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to classify tweets according to evidences of hacking attacks or data breaches
 *
 * @author Isuru Chandima
 * @author Warunika Amali
 */
public class TweetEvidenceClassifier extends LeakHawkClassifier {

    /**
     * These identifiers are defined to identify output streams from TweetEvidenceClassifier
     * to the TweetContentClassifier or to the UrlProcessor
     */
    private String tweetsNormalFlow = "tweets-normal-flow";
    private String tweetsUrlFlow = "tweets-url-flow";

    private RandomForest tclassifier;

    /**
     * Lists that defines attributes of the arff file
     */
    private Pattern cfSymbalPattern;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private ArrayList<Pattern> trigramPatternList;

    private Pattern relatedPattern1;
    /**
     * Database connection
     */
    private Connection connection;
    /**
     * Header of the arff file
     */
    private String headingEvidenceClassifier = "@relation EC\n" +
            "\n" +
            "@attribute $EC1 numeric\n" +
            "@attribute $EC2 numeric\n" +
            "@attribute $EC3 numeric\n" +
            "@attribute $EC4 numeric\n" +
            "@attribute $EC5 numeric\n" +
            "@attribute $EC6 numeric\n" +
            "@attribute $EC7 numeric\n" +
            "@attribute $EC8 numeric\n" +
            "@attribute $EC9 numeric\n" +
            "@attribute $EC10 numeric\n" +
            "@attribute $EC11 numeric\n" +
            "@attribute $EC12 numeric\n" +
            "@attribute $EC13 numeric\n" +
            "@attribute $EC14 numeric\n" +
            "@attribute $EC15 numeric\n" +
            "@attribute $EC16 numeric\n" +
            "@attribute $EC17 numeric\n" +
            "@attribute $EC18 numeric\n" +
            "@attribute $EC19 numeric\n" +
            "@attribute $EC20 numeric\n" +
            "@attribute $EC21 numeric\n" +
            "@attribute $EC22 numeric\n" +
            "@attribute $EC23 numeric\n" +
            "@attribute $EC24 numeric\n" +
            "@attribute $EC25 numeric\n" +
            "@attribute $EC26 numeric\n" +
            "@attribute $EC27 numeric\n" +
            "@attribute $EC28 numeric\n" +
            "@attribute $EC29 numeric\n" +
            "@attribute $EC30 numeric\n" +
            "@attribute $EC31 numeric\n" +
            "@attribute $EC32 numeric\n" +
            "@attribute $EC33 numeric\n" +
            "@attribute $EC34 numeric\n" +
            "@attribute $EC35 numeric\n" +
            "@attribute $EC36 numeric\n" +
            "@attribute $EC37 numeric\n" +
            "@attribute $EC38 numeric\n" +
            "@attribute $EC39 numeric\n" +
            "@attribute $EC40 numeric\n" +
            "@attribute $EC41 numeric\n" +
            "@attribute $EC42 numeric\n" +
            "@attribute $EC43 numeric\n" +
            "@attribute $EC44 numeric\n" +
            "@attribute $EC45 numeric\n" +
            "@attribute $EC46 numeric\n" +
            "@attribute $EC47 numeric\n" +
            "@attribute $EC48 numeric\n" +
            "@attribute $EC49 numeric\n" +
            "@attribute $EC50 numeric\n" +
            "@attribute $EC51 numeric\n" +
            "@attribute $EC52 numeric\n" +
            "@attribute $EC53 numeric\n" +
            "@attribute $EC54 numeric\n" +
            "@attribute $EC55 numeric\n" +
            "@attribute $EC56 numeric\n" +
            "@attribute $EC57 numeric\n" +
            "@attribute $EC58 numeric\n" +
            "@attribute $EC59 numeric\n" +
            "@attribute $EC60 numeric\n" +
            "@attribute $EC61 numeric\n" +
            "@attribute $EC62 numeric\n" +
            "@attribute $EC63 numeric\n" +
            "@attribute $EC64 numeric\n" +
            "@attribute $EC65 numeric\n" +
            "@attribute $EC66 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";

    public TweetEvidenceClassifier() {
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH+"/Evidence_twitter.model");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> unigramList = new ArrayList<>();
        unigramList.add("Anonymous");
        unigramList.add("AnonSec");
        unigramList.add("AnonGhost");
        unigramList.add("ANONYMOUSSRILANKA");
        unigramList.add("Anonhack");
        unigramList.add("OPlanka");
        unigramList.add("cc_dump");
        unigramList.add("card_hack");
        unigramList.add("db_dump");
        unigramList.add("defaced");
        unigramList.add("email-list");
        unigramList.add("email_hack");
        unigramList.add("hacked");
        unigramList.add("leaked");
        unigramList.add("hack");
        unigramList.add("leak");
        unigramList.add("exploit");
        unigramList.add("attack");
        unigramList.add("attacked");
        unigramList.add("#opSriLanka");
        unigramList.add("#anonymous");
        unigramList.add("credit_card");
        unigramList.add("UGLegion");
        unigramList.add("RetrOHacK");
        unigramList.add("dns-brute");
        unigramList.add("dnsrecon");
        unigramList.add("SQLmap");
        unigramList.add("card_dump");

        ArrayList<String> bigramList = new ArrayList<>();
        bigramList.add("email dumps");
        bigramList.add("DNS fuck3d");
        bigramList.add("leaked email");
        bigramList.add("email hack");
        bigramList.add("leaked emails");
        bigramList.add("cache poisoning");
        bigramList.add("database dumped");
        bigramList.add("leaked by");
        bigramList.add("site deface");
        bigramList.add("database dump");
        bigramList.add("db dumped");
        bigramList.add("db leak");
        bigramList.add("domain hack");
        bigramList.add("db hack");
        bigramList.add("emails hack");
        bigramList.add("email leak");
        bigramList.add("emails leak");
        bigramList.add("DNS LeAkEd");
        bigramList.add("database hack");
        bigramList.add("email");
        bigramList.add("email");
        bigramList.add("email");
        bigramList.add("Pwned by");
        bigramList.add("Private key");
        bigramList.add("Password leak");
        bigramList.add("password dump");
        bigramList.add("credential leak");
        bigramList.add("credential dump");
        bigramList.add("Credit card");
        bigramList.add("Card dump");
        bigramList.add("cc dump");
        bigramList.add("website hacked");
        bigramList.add("email dump");
        bigramList.add("emails dump");

        ArrayList<String> trigramList = new ArrayList<>();
        trigramList.add("website hacked by");
        trigramList.add("site hacked by");
        trigramList.add("model base leak");

        relatedPattern1 = Pattern.compile("SQL_Injection|SQLi|SQL-i|Blind SQL-i|SQL", Pattern.CASE_INSENSITIVE);

        unigramPatternList = new ArrayList<>();
        for (String word : unigramList) {
            unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<>();
        for (String word : bigramList) {
            bigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<>();
        for (String word : trigramList) {
            trigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

    }

    private Pattern getCorrectPatten(String word, int type) {
        return Pattern.compile(word.replaceAll("\\|", "\b|\b"), type);
    }

    @Override
    public void prepareClassifier() {
        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getBoltName() {
        return LeakHawkConstant.TWEETS_EVIDENCE_CLASSIFIER;
    }

    @Override
    public void classifyPost(Post post) {

        EvidenceModel evidenceModel = new EvidenceModel();
        post.setEvidenceModel(evidenceModel);

        boolean evidenceFound = true;
        if(!post.isPossiblySensitive()) {
            evidenceFound = isPassedEvidenceClassifier(post.getUser(), post.getPostText(), evidenceModel);
        }
        evidenceModel.setEvidenceFound(evidenceFound);

        if (evidenceFound) {
            // If an evidence found in the post, check if it contains any other links. (urls)
            // For that process, send the post to another bolt for further processes
            increaseOutCount();
            post.setNextOutputStream(LeakHawkConstant.T_EVIDENCE_CLASSIFIER_TO_URL_PROCESSOR);
        } else {
            // No evidence found, send the post through the normal flow
            post.setNextOutputStream(LeakHawkConstant.T_EVIDENCE_CLASSIFIER_TO_T_CONTENT_CLASSIFIER);
        }
    }

    /**
     * Finds whether there's an evidence of hacking attack or not
     *
     * @param user
     * @param post
     * @param evidenceModel
     * @return
     */
    private boolean isPassedEvidenceClassifier(String user, String post, EvidenceModel evidenceModel) {

        post = post.toLowerCase();
        user = user.toLowerCase();
        boolean evidenceFound;

        evidenceFound = isEvidenceFound(post);

        try {
            ResultSet data = DBHandle.getData(connection, "SELECT user FROM incident");
            while (data.next()) {
                String userFromDB = data.getString("user");
                //check user of the tweet.
                if (user.equals(userFromDB.toLowerCase())) {
                    evidenceModel.setClassifier1Passed(true);
                    evidenceFound = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return evidenceFound;
    }

    /**
     * Classify the new tweets
     *
     * @param text
     * @return
     */
    private boolean isEvidenceFound(String text) {
        try {
            // convert String into InputStream
            String result = createARFF(text);
            InputStream is = new ByteArrayInputStream(result.getBytes());

            // wrap it with buffered reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            //convert into a set of instances
            Instances unlabeled = new Instances(reader);
            reader.close();
            //set the class index to last value of the instance
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // create copy
            Instances labeled = new Instances(unlabeled);

            //set options for the classifier
            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            //predict class for the unseen text
            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
            labeled.instance(0).setClassValue(pred);

            //get the predicted class value
            String classLabel = unlabeled.classAttribute().value((int) pred);

            //if class is pos there's an evidence found
            if ("pos".equals(classLabel)) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create arff file for the predicting text
     *
     * @param text
     * @return
     */
    public String createARFF(String text) {
        String feature_list = "";

        //check the pattern match for text and title for all the cases
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

        Matcher matcherCF = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherCF) + ",";
        //add unknown class for the feature vector
        feature_list += "?";
        return headingEvidenceClassifier + feature_list;
    }

    private int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkConstant.T_EVIDENCE_CLASSIFIER_TO_T_CONTENT_CLASSIFIER);
        outputStream.add(LeakHawkConstant.T_EVIDENCE_CLASSIFIER_TO_URL_PROCESSOR);

        return outputStream;
    }
}
