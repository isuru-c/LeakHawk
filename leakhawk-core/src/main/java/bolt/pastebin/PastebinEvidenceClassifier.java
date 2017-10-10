/*
 *     Copyright 2017 SWIS
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

package bolt.pastebin;

import bolt.core.LeakHawkEvidenceClassifier;
import model.EvidenceModel;
import model.Post;
import db.DBConnection;
import db.DBHandle;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import weka.classifiers.misc.SerializedClassifier;
import weka.core.Instances;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sewwandi
 */
public class PastebinEvidenceClassifier extends LeakHawkEvidenceClassifier {

    /**
     * These identifiers are defined to identify output streams from PastebinEvidenceClassifier
     * to the PastebinContentClassifier or to the UrlProcessor
     */
    private String pastebinNormalFlow = "pastebin-normal-flow";
    private String pastebinUrlFlow = "pastebin-url-flow";

    /**
     * Lists used to create attributes in arff file
     */
    private ArrayList<String> keyWordList1;
    private ArrayList<String> keyWordList2;
    //private ArrayList<String> keyWordList3;
    private ArrayList<String> keyWordList4;
    private ArrayList<Pattern> hackingAttackPatternList;
    private ArrayList<Pattern> securityToolPatternList;
    //private ArrayList<Pattern> securityVulnerabilityPatternList;
    private ArrayList<Pattern> hackerPatternList;
    private Pattern relatedPattern1;
    /**
     * Database connection
     */
    private Connection connection;
    /**
     * ML model load using serialzed classifer
     */
    private static SerializedClassifier sclassifier;

    /**
     * Attributes of arff file
     */
    private String headingEvidenceClassifier = "@relation EC\n" +
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
            "@attribute $EC67 numeric\n" +
            "@attribute $EC68 numeric\n" +
            "@attribute $EC69 numeric\n" +
            "@attribute $EC70 numeric\n" +
            "@attribute $EC71 numeric\n" +
            "@attribute $EC72 numeric\n" +
            "@attribute $EC73 numeric\n" +
            "@attribute $EC74 numeric\n" +
            "@attribute $EC75 numeric\n" +
            "@attribute $EC76 numeric\n" +
            "@attribute $EC77 numeric\n" +
            "@attribute $EC78 numeric\n" +
            "@attribute $EC79 numeric\n" +
            "@attribute $EC80 numeric\n" +
            "@attribute $EC81 numeric\n" +
            "@attribute $EC82 numeric\n" +
            "@attribute $EC83 numeric\n" +
            "@attribute $EC84 numeric\n" +
            "@attribute $EC85 numeric\n" +
            "@attribute $EC86 numeric\n" +
            "@attribute $EC87 numeric\n" +
            "@attribute $EC88 numeric\n" +
            "@attribute $EC89 numeric\n" +
            "@attribute $EC90 numeric\n" +
            "@attribute $EC91 numeric\n" +
            "@attribute $EC92 numeric\n" +
            "@attribute $EC93 numeric\n" +
            "@attribute $EC94 numeric\n" +
            "@attribute $EC95 numeric\n" +
            "@attribute $EC96 numeric\n" +
            "@attribute $EC97 numeric\n" +
            "@attribute $EC98 numeric\n" +
            "@attribute $EC99 numeric\n" +
            "@attribute $EC100 numeric\n" +
            "@attribute $EC101 numeric\n" +
            "@attribute $EC102 numeric\n" +
            "@attribute $EC103 numeric\n" +
            "@attribute $EC104 numeric\n" +
            "@attribute $EC105 numeric\n" +
            "@attribute $EC106 numeric\n" +
            "@attribute $EC107 numeric\n" +
            "@attribute $EC108 numeric\n" +
            "@attribute $EC109 numeric\n" +
            "@attribute $EC110 numeric\n" +
            "@attribute $EC111 numeric\n" +
            "@attribute $EC112 numeric\n" +
            "@attribute $EC113 numeric\n" +
            "@attribute $EC114 numeric\n" +
            "@attribute $EC115 numeric\n" +
            "@attribute $EC116 numeric\n" +
            "@attribute $EC117 numeric\n" +
            "@attribute $EC118 numeric\n" +
            "@attribute $EC119 numeric\n" +
            "@attribute $EC120 numeric\n" +
            "@attribute $EC121 numeric\n" +
            "@attribute $EC122 numeric\n" +
            "@attribute $EC123 numeric\n" +
            "@attribute $EC124 numeric\n" +
            "@attribute $EC125 numeric\n" +
            "@attribute $EC126 numeric\n" +
            "@attribute $EC127 numeric\n" +
            "@attribute $EC128 numeric\n" +
            "@attribute $EC129 numeric\n" +
            "@attribute $EC130 numeric\n" +
            "@attribute $EC131 numeric\n" +
            "@attribute $EC132 numeric\n" +
            "@attribute $EC133 numeric\n" +
            "@attribute $EC134 numeric\n" +
            "@attribute $EC135 numeric\n" +
            "@attribute $EC136 numeric\n" +
            "@attribute $EC137 numeric\n" +
            "@attribute $EC138 numeric\n" +
            "@attribute $EC139 numeric\n" +
            "@attribute $EC140 numeric\n" +
            "@attribute $EC141 numeric\n" +
            "@attribute $EC142 numeric\n" +
            "@attribute $EC143 numeric\n" +
            "@attribute $EC144 numeric\n" +
            "@attribute $EC145 numeric\n" +
            "@attribute $EC146 numeric\n" +
            "@attribute $EC147 numeric\n" +
            "@attribute $EC148 numeric\n" +
            "@attribute $EC149 numeric\n" +
            "@attribute $EC150 numeric\n" +
            "@attribute $EC151 numeric\n" +
            "@attribute $EC152 numeric\n" +
            "@attribute $EC153 numeric\n" +
            "@attribute $EC154 numeric\n" +
            "@attribute $EC155 numeric\n" +
            "@attribute $EC156 numeric\n" +
            "@attribute $EC157 numeric\n" +
            "@attribute $EC158 numeric\n" +
            "@attribute $EC159 numeric\n" +
            "@attribute $EC160 numeric\n" +
            "@attribute $EC161 numeric\n" +
            "@attribute $EC162 numeric\n" +
            "@attribute $EC163 numeric\n" +
            "@attribute $EC164 numeric\n" +
            "@attribute $EC165 numeric\n" +
            "@attribute $EC166 numeric\n" +
            "@attribute $EC167 numeric\n" +
            "@attribute $EC168 numeric\n" +
            "@attribute $EC169 numeric\n" +
            "@attribute $EC170 numeric\n" +
            "@attribute $EC171 numeric\n" +
            "@attribute $EC172 numeric\n" +
            "@attribute $EC173 numeric\n" +
            "@attribute $EC174 numeric\n" +
            "@attribute $EC175 numeric\n" +
            "@attribute $EC176 numeric\n" +
            "@attribute $EC177 numeric\n" +
            "@attribute $EC178 numeric\n" +

            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";

    public PastebinEvidenceClassifier() {
        //ML model loaded
        try {
            sclassifier = new SerializedClassifier();
            sclassifier.setModelFile(new File(this.getClass().getClassLoader().getResource("EviC.model").getFile()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        keyWordList1 = new ArrayList(Arrays.asList("hacked", "leaked", "hack", "leak", "exploit", "exploits", "attack", "attacked", "Pwned by", "Doxed", "Ow3ned", "pawned by", "Server Rootéd", "#opSriLanka", "#OPlanka", "#anonymous", "Private key", "Password leak", "password dump", "credential leak", "credential dump", "Credit card", "Card dump ", " cc dump ", " credit_card", "card_dump", "working_card", "cc_dump", "skimmed", "card_hack", "sited hacked by", "websited hacked by", "website hacked by", "site hacked by", "websited hacked", "domain hack", "defaced", "leaked by", "site deface", "mass deface", "database dump", "database dumped", "db dumped", "db_dump", "db leak", "model base dump", "model base leak", "database hack", "db hack", "login dump", "DNS LeAkEd", "DNS fuck3d", "zone transfer", "DNS Enumeration", "Enumeration Attack", "cache snooping", "cache poisoning", "email hack", "emails hack", "emails leak|email leak", "email dump", "emails dump", "email dumps", "email-list", "leaked email|leaked emails", "email_hack"));
        keyWordList2 = new ArrayList(Arrays.asList("dns-brute", "dnsrecon", "fierce", "Dnsdict6", "axfr", "SQLmap"));
        keyWordList4 = new ArrayList(Arrays.asList("UGLegion", "RetrOHacK", "Anonhack", "Anonymous", "AnonSec", "AnonGhost", "ANONYMOUSSRILANKA", "W3BD3F4C3R", "SLCYBERARMY", "DAVYJONES", "BLACKHATANON", "ANUARLINUX", "UGLEGION", "HUSSEIN98D", "We_Are_Anonymous", "We_do_not_Forget", "We_do_not_Forgive", "Laughing_at_your_security_since_2012", "AnonGhost_is_Everywhere"));
        relatedPattern1 = Pattern.compile("SQL_Injection|SQLi|SQL-i|Blind SQL-i|SQL", Pattern.CASE_INSENSITIVE);

        hackingAttackPatternList = new ArrayList<>();
        securityToolPatternList = new ArrayList<>();
        hackerPatternList = new ArrayList<>();

        for (String word : keyWordList1) {
            hackingAttackPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        for (String word : keyWordList2) {
            securityToolPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        for (String word : keyWordList4) {
            hackerPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }


    }

    @Override
    public void prepareEvidenceClassifier() {

        /*loader = new TextDirectoryLoader();
        stopWordList = Arrays.asList("a","about","above","after","again"," against","all","am","an","and","any","are","aren't","as","at","be","because","been","before","being","below","between","both","but","by","can't","cannot","could","couldn't","did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from","further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her","here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my","myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours","ourselves","out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such","than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they","they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was","wasn't","we","we'd","we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's","which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves");
        regex = "[0-9]+";
        regex1 = "[.,?!@#%;:'\"\\-]";*/

        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeEvidenceClassifier(Post post, EvidenceModel evidenceModel, Tuple tuple, OutputCollector collector) {
        boolean evidenceFound = isPassedEvidenceClassifier(post.getUser(), post.getTitle(), post.getPostText(), evidenceModel);

        evidenceModel.setEvidenceFound(evidenceFound);

        if (evidenceFound) {
            // If an evidence found in the post, check if it contains any other links. (urls)
            // For that process, send the post to another bolt for further processes
            collector.emit(pastebinUrlFlow, tuple, new Values(post));
        }else {
            // No evidence found, send the post through the normal flow
            collector.emit(pastebinNormalFlow, tuple, new Values(post));
        }
    }

    /**
     * Checks whether post passes Evidence classifer
     *
     * @param user
     * @param title
     * @param post
     * @param evidenceModel
     * @return
     */
    private boolean isPassedEvidenceClassifier(String user, String title, String post, EvidenceModel evidenceModel) {

        title = title.toLowerCase();
        post = post.toLowerCase();

        boolean evidenceFound;

        //#U1-USER: Does the user, seems suspicious?
        //need to compare with the database - percentage

        //#E1 	SUBJECT:Is there any evidence of a hacking attack on the subject?
        //#E2 	SUBJECT:Are there any signs of usage of a security tool?
        //#E3 	SUBJECT:Are there any signs of security vulnerability?
        //#E4 	SUBJECT:Evidence of a Hacker involvement/Hacktivist movement?
        //#E5 	BODY:	Is there any evidence of a hacking attack in the body text?
        //#E6 	BODY:	Are there any signs of usage of a security tool in the body text?
        //#E7	BODY:	Are there any signs of security vulnerability in the body text?
        //#E8	BODY:	Are there any signs of security vulnerability in the body text?
        try {
            ResultSet data = DBHandle.getData(connection, "SELECT user FROM Incident");
            while (data.next()) {
                String userFromDB = data.getString("user");
                if (title.contains(userFromDB.toLowerCase())) {
                    evidenceModel.setClassifier1Passed(true);
                    evidenceFound = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        evidenceFound = isEvidenceFound(post, title);


        return evidenceFound;
    }

    /**
     * Classifes unseen instances
     *
     * @param text
     * @param title
     * @return
     */
    private boolean isEvidenceFound(String text, String title) {
        try {
            // convert String into InputStream
            String result = createARFF(text, title);
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
            sclassifier.setOptions(options);

            //predict class for the unseen text
            double pred = sclassifier.classifyInstance(unlabeled.instance(0));
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
     * create arff file for the predicting text and the title
     *
     * @param text
     * @param title
     * @return
     */
    public String createARFF(String text, String title) {
        String feature_list = "";

        //check the pattern match for text and title for all the cases
        for (Pattern pattern : hackingAttackPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : hackingAttackPatternList) {
            Matcher matcher = pattern.matcher(title);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : securityToolPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : hackerPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : hackerPatternList) {
            Matcher matcher = pattern.matcher(title);
            feature_list += getMatchingCount(matcher) + ",";
        }

        Matcher matcherEC = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherEC) + ",";

        matcherEC = relatedPattern1.matcher(title);
        feature_list += getMatchingCount(matcherEC) + ",";

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

    /**
     * In the current topology, output of the PastebinEvidenceClassifier is connected to two
     * different bolts [PastebinContentClassifier and UrlProcessor] depend on the content
     * of the post. {existence of evidence or not] Hence two output streams are defined in here.
     *
     * pastebinNormalFlow - when there is no evidence in the post, it is forwarded to
     *                      PastebinContentClassier in the LeakHawk core topology
     * pastebinUrlFlow - if it turns to be true in evidence classification, content is needed
     *                  to check for urls. For that post is forwarded to UrlProcessor.
     *
     * These exact identifiers are needs to be used when creating the storm topology.
     *
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(pastebinNormalFlow, new Fields("post"));
        outputFieldsDeclarer.declareStream(pastebinUrlFlow, new Fields("post"));
    }
}
