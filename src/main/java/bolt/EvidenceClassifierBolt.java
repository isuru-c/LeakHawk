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

package bolt;

import model.EvidenceModel;
import model.Post;
import db.DBConnection;
import db.DBHandle;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.StopwordsHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class EvidenceClassifierBolt extends BaseRichBolt {

    private OutputCollector collector;
    private ArrayList<String> keyWordList1;
    private ArrayList<String> keyWordList2;
    private ArrayList<String> keyWordList3;
    private ArrayList<String> keyWordList4;
    private ArrayList<String> keyWordList5;
    private ArrayList<String> keyWordList6;
    private ArrayList<String> keyWordList7;
    private ArrayList<String> keyWordList8;
    private Connection connection;
    private TextDirectoryLoader loader;
    private Instances dataRaw;
    private StringToWordVector filter;
    private Instances dataFiltered;
    private List<String> stopWordList;
    private String regex;
    private String regex1;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

        loader = new TextDirectoryLoader();
        stopWordList = Arrays.asList("a","about","above","after","again"," against","all","am","an","and","any","are","aren't","as","at","be","because","been","before","being","below","between","both","but","by","can't","cannot","could","couldn't","did","didn't","do","does","doesn't","doing","don't","down","during","each","few","for","from","further","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","he's","her","here","here's","hers","herself","him","himself","his","how","how's","i","i'd","i'll","i'm","i've","if","in","into","is","isn't","it","it's","its","itself","let's","me","more","most","mustn't","my","myself","no","nor","not","of","off","on","once","only","or","other","ought","our","ours","ourselves","out","over","own","same","shan't","she","she'd","she'll","she's","should","shouldn't","so","some","such","than","that","that's","the","their","theirs","them","themselves","then","there","there's","these","they","they'd","they'll","they're","they've","this","those","through","to","too","under","until","up","very","was","wasn't","we","we'd","we'll","we're","we've","were","weren't","what","what's","when","when's","where","where's","which","while","who","who's","whom","why","why's","with","won't","would","wouldn't","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves");
        regex = "[0-9]+";
        regex1 = "[.,?!@#%;:'\"\\-]";

        keyWordList1 = new ArrayList(Arrays.asList("Hacked", "leaked by", "Pwned by", "Doxed", "Ow3ned", "pawned by", "Server Rootéd", "#opSriLanka", "#OPlanka", "#anonymous", "Private key", "Password leak", "password dump", "credential leak", "credential dump", "Credit card", "Card dump ", " cc dump ", " credit_card", "card_dump", "working_card", "cc_dump", "skimmed", "card_hack", "sited hacked by", "websited hacked by", "website hacked by", "site hacked by", "websited hacked", "domain hack", "defaced", "leaked by", "site deface", "mass deface", "database dump", "database dumped", "db dumped", "db_dump", "db leak", "model base dump", "model base leak", "database hack", "db hack", "login dump", "DNS LeAkEd", "DNS fuck3d", "zone transfer", "DNS Enumeration", "Enumeration Attack", "cache snooping", "cache poisoning", "email hack", "emails hack", "emails leak,email leak", "email dump", "emails dump", "email dumps", "email-list", "leaked email,leaked emails", "email_hack"));
        keyWordList2 = new ArrayList(Arrays.asList("dns-brute", "dnsrecon", "fierce", "Dnsdict6", "axfr", "SQLmap"));
        keyWordList3 = new ArrayList(Arrays.asList("SQL_Injection", "SQLi", "SQL-i", "Blind SQL-i"));
        keyWordList4 = new ArrayList(Arrays.asList("UGLegion", "RetrOHacK", "Anonymous", "AnonSec", "AnonGhost", "ANONYMOUSSRILANKA", "W3BD3F4C3R", "SLCYBERARMY", "DAVYJONES", "BLACKHATANON", "ANUARLINUX", "UGLEGION", "HUSSEIN98D", "We_Are_Anonymous", "We_do_not_Forget", "We_do_not_Forgive", "Laughing_at_your_security_since_2012", "AnonGhost_is_Everywhere"));
        keyWordList5 = new ArrayList(Arrays.asList("Hacked", "leaked by", "Pwned by", "Doxed", "Ow3ned", "pawned by", "Server Rootéd", "#opSriLanka", "#OPlanka", "#anonymous", "Private key", "Password leak", "password dump", "credential leak", "credential dump", "Credit card", "Card dump ", " cc dump ", " credit_card", "card_dump", "working_card", "cc_dump", "skimmed", "card_hack", "sited hacked by", "websited hacked by", "website hacked by", "site hacked by", "websited hacked", "domain hack", "defaced", "leaked by", "site deface", "mass deface", "database dump", "database dumped", "db dumped", "db dump", "db leak", "model base dump", "model base leak", "database hack", "db hack", "login dump", "DNS LeAkEd", "DNS fuck3d", "zone transfer", "DNS Enumeration", "Enumeration Attack", "cache snooping", "cache poisoning", "email hack", "emails hack", "emails leak,email leak", "email dump", "emails dump", "email dumps", "email-list", "leaked email,leaked emails", "email_hack"));
        keyWordList6 = new ArrayList(Arrays.asList("dns-brute", "dnsrecon", "fierce", "Dnsdict6", "axfr", "SQLmap"));
        keyWordList7 = new ArrayList(Arrays.asList("SQL Injection", "SQLi", "SQL-i", "Blind SQL-i"));
        keyWordList8 = new ArrayList(Arrays.asList("UGLegion", "RetrOHacK", "Anonymous", "AnonSec", "AnonGhost", "ANONYMOUSSRILANKA", "W3BD3F4C3R", "SLCYBERARMY", "DAVYJONES", "BLACKHATANON", "ANUARLINUX", "UGLEGION", "HUSSEIN98D", "We Are Anonymous", "We do not Forget, We do not Forgive", "Laughing at your security since 2012", "AnonGhost is Everywhere"));

        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void execute(Tuple tuple) {

        Post post = (Post)tuple.getValue(0);

        EvidenceModel evidenceModel = new EvidenceModel();
        post.setEvidenceModel(evidenceModel);

        Boolean evidenceFound = isPassedEvidenceClassifier(post.getUser(), post.getTitle(), post.getPostText(), evidenceModel);

        evidenceModel.setEvidenceFound(evidenceFound);

        post.setEvidenceClassifierPassed();

        collector.emit(tuple, new Values(post));

        collector.ack(tuple);

    }

    private boolean isPassedEvidenceClassifier(String user, String title, String post, EvidenceModel evidenceModel) {

        title = title.toLowerCase();
        post = post.toLowerCase();

        boolean evidenceFound = false;

        //#U1-USER: Does the user, seems suspicious?
        //need to compare with the database - percentage

        //#E1 	SUBJECT:Is there any evidence of a hacking attack on the subject?
        for (String i : keyWordList1) {
            if (title.contains(i.toLowerCase())) {
                evidenceModel.setClassifier1Passed(true);
                evidenceFound = true;
            }
        }

        try {
            ResultSet data = DBHandle.getData(connection, "SELECT user FROM Incident");
            while(data.next()){
                String userFromDB = data.getString("user");
                if (title.contains(userFromDB.toLowerCase())) {
                    evidenceModel.setClassifier1Passed(true);
                    evidenceFound = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //#E2 	SUBJECT:Are there any signs of usage of a security tool?
        for (String i : keyWordList2) {
            if (title.contains(i.toLowerCase())) {
                evidenceModel.setClassifier2Passed(true);
                evidenceFound = true;
            }
        }

        //#E3 	SUBJECT:Are there any signs of security vulnerability?
        for (String i : keyWordList3) {
            if (title.contains(i.toLowerCase())) {
                evidenceModel.setClassifier3Passed(true);
                evidenceFound = true;
            }
        }

        //#E4 	SUBJECT:Evidence of a Hacker involvement/Hacktivist movement?
        for (String i : keyWordList4) {
            if (title.contains(i.toLowerCase())) {
                evidenceModel.setClassifier4Passed(true);
                evidenceFound = true;
            }
        }

        //#E5 	BODY:	Is there any evidence of a hacking attack in the body text?
        for (String i : keyWordList5) {
            if (post.contains(i.toLowerCase())) {
                evidenceModel.setClassifier5Passed(true);
                evidenceFound = true;
            }
        }

        //#E6 	BODY:	Are there any signs of usage of a security tool in the body text?
        for (String i : keyWordList6) {
            if (post.contains(i.toLowerCase())) {
                evidenceModel.setClassifier6Passed(true);
                evidenceFound = true;
            }
        }

        //#E7	BODY:	Are there any signs of security vulnerability in the body text?
        for (String i : keyWordList7) {
            if (post.contains(i.toLowerCase())) {
                evidenceModel.setClassifier7Passed(true);
                evidenceFound = true;
            }
        }

        //#E8	BODY:	Are there any signs of security vulnerability in the body text?
        for (String i : keyWordList8) {
            if (post.toLowerCase().contains(i.toLowerCase())) {
                evidenceModel.setClassifier8Passed(true);
                evidenceFound = true;
            }
        }

        return evidenceFound;
    }

    private void buildClassifier() {
        try{
            loader.setDirectory(new File("~/IdeaProjects/LeakHawk/dataset/"));
            dataRaw = loader.getDataSet();

            filter = new StringToWordVector();

            StopwordsHandler stopwordsHandler = new StopwordsHandler() {
                Matcher matcher;

                //@Override
                public boolean isStopword(String s) {
                    if(stopWordList.contains(s) || s.length()<3 || s.matches(regex1) || s.matches(regex)) //matcher == p.matcher(s)
                        return true;
                    else return false;
                }
            };

            filter.setStopwordsHandler(stopwordsHandler);
            SnowballStemmer stemmer = new SnowballStemmer();
            filter.setStemmer(stemmer);
            filter.setLowerCaseTokens(true);
            filter.setTFTransform(true);
            filter.setIDFTransform(true);
            filter.setInputFormat(dataRaw);

            dataFiltered = Filter.useFilter(dataRaw, filter);
            System.out.println("\n\nFiltered data:\n\n" + dataFiltered);

            NaiveBayesMultinomial classifier = new NaiveBayesMultinomial();
            classifier.buildClassifier(dataFiltered);
            //System.out.println("\n\nClassifier model:\n\n" + classifier);

            Evaluation evaluation = new Evaluation(dataFiltered);
            evaluation.crossValidateModel(classifier,dataFiltered,10,new Random());
            System.out.println(evaluation.toSummaryString()+evaluation.toMatrixString());

            // generate curve
           /* ThresholdCurve tc = new ThresholdCurve();
            int classIndex = 0;
            Instances result = tc.getCurve(evaluation.predictions(), classIndex);

            // plot curve
            ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
            vmc.setROCString("(Area under ROC = " +
                    Utils.doubleToString(tc.getROCArea(result), 4) + ")");
            vmc.setName(result.relationName());
            PlotData2D tempd = new PlotData2D(result);
            tempd.setPlotName(result.relationName());
            tempd.addInstanceNumberAttribute();
            // specify which points are connected
            boolean[] cp = new boolean[result.numInstances()];
            for (int n = 1; n < cp.length; n++)
                cp[n] = true;
            tempd.setConnectPoints(cp);
            // add plot
            vmc.addPlot(tempd);

            // display curve
            String plotName = vmc.getName();
            final javax.swing.JFrame jf =
                    new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
            jf.setSize(500,400);
            jf.getContentPane().setLayout(new BorderLayout());
            jf.getContentPane().add(vmc, BorderLayout.CENTER);
            jf.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    jf.dispose();
                }
            });
            jf.setVisible(true);

*/

        }
        catch (IOException ex){
            System.out.println("IOException");
        }catch(Exception ex1){

        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
