import classifiers.EvidenceModel;
import classifiers.ContentModel;
import classifiers.Predictor.SensitivityModel;
import data.Post;
import db.DBConnection;
import db.DBHandle;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class EndOfClassifierBolt extends BaseRichBolt {

    OutputCollector collector;
    static int count = 0;
    private int creditCardNumberCount;
    private int URLratio;
    private int email_hash_count;
    Pattern ccCardPattern;
    EvidenceModel evidenceModel;
    ContentModel contentModel;
    Connection connection;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);
        evidenceModel = post.getEvidenceModel();
        contentModel = post.getContentModel();

        SensitivityModel sensitivityModel = predictSensitivity(post.getPostText());

        if (sensitivityModel.isEvidenceClassifier() || sensitivityModel.isContentClassifier() && sensitivityModel.getLevel()>=2) {


            try {
                DBHandle.setData(connection,"INSERT INTO Incident VALUES ('"+post.getKey()+"','"+post.getUser()+"','"+post.getTitle()+"','"
                        +post.getPostType()+"','"+post.getDate()+"',"+sensitivityModel.getLevel()+","+sensitivityModel.isContentClassifier()
                        +","+sensitivityModel.isEvidenceClassifier()+",'"+sensitivityModel.getPredictClass()+"')");
            } catch (SQLException e) {
                e.printStackTrace();
            }


            System.out.println("\nPost  : " + post.getKey());
            System.out.println("\nEvidence Found  : " + sensitivityModel.isEvidenceClassifier());
            System.out.println("\nContent Found  : " + sensitivityModel.isContentClassifier());


            System.out.println("Sensitivity level of post is :" + sensitivityModel.getLevel() + "\n");
            System.out.println("Sensitivity class is  :" + sensitivityModel.getPredictClass() + "\n");
        }
//        System.out.println("\nKey: " + post.getKey() + "\nDate: " + post.getDate() + "\nUser: " + post.getUser() + "\nTitle: " + post.getTitle() + "\n" + post.getPostText() + "\nCount: " + count++);
        collector.ack(tuple);
    }


    public int getCreditCardNumberCount() {
        return creditCardNumberCount;
    }

    public void setCreditCardNumberCount(int creditCardNumberCount) {
        this.creditCardNumberCount = creditCardNumberCount;
    }

    public void setURLratio(int URLratio) {
        this.URLratio = URLratio;
    }

    public void setEmail_hash_count(int email_hash_count) {
        this.email_hash_count = email_hash_count;
    }

    public int getURLratio() {
        return URLratio;
    }

    public int getEmail_hash_count() {
        return email_hash_count;
    }

    public SensitivityModel predictSensitivity(String post) {
        SensitivityModel sensitivityModel = new SensitivityModel();
        sensitivityModel.setContentClassifier(contentModel.isContentFound());
        sensitivityModel.setEvidenceClassifier(evidenceModel.isEvidenceFound());

        if ((contentModel.isContentFound() && evidenceModel.isEvidenceFound())) {
            if (contentModel.isPassedCC()) {
                setCreditCardNumberCount(extractCCNumberCount(post));
                if ((creditCardNumberCount < 5)) {
                    sensitivityModel.setLevel(1);
                } else if ((creditCardNumberCount < 20) && (creditCardNumberCount > 5)) {
                    sensitivityModel.setLevel(2);
                } else if (creditCardNumberCount > 20) {
                    sensitivityModel.setLevel(3);
                }
                sensitivityModel.setPredictClass("CC");
            } else if (contentModel.isPassedPK()) {
                sensitivityModel.setLevel(3);
                sensitivityModel.setPredictClass("PK");
            } else if (contentModel.isPassedCF()) {
                if (post.contains("enable password")) {
                    sensitivityModel.setLevel(3);
                } else {
                    sensitivityModel.setLevel(1);
                }
                sensitivityModel.setPredictClass("CF");
            } else if (contentModel.isPassedDB()) {
                sensitivityModel.setLevel(2);
                sensitivityModel.setPredictClass("DB");

            } else if (contentModel.isPassedDA()) {
                //System.out.println("CONTENT: Possible DNS attack!");
                ArrayList<String> DAlist = new ArrayList<String>(Arrays.asList("lanka", "lk", "ceylon", "sinhala", "buddhist", "colombo", "kandy", "kurunegala", "gampaha", "mahinda", "sirisena", "ranil"));
                int domainCount = 0;
                for (String i : DAlist) {
                    if (post.contains(i)) {
                        count++;
                    }
                }
                if (domainCount < 10) {
                    sensitivityModel.setLevel(2);
                    sensitivityModel.setPredictClass("DA");
                } else if (domainCount >= 10) {
                    sensitivityModel.setLevel(3);
                    sensitivityModel.setPredictClass("DA");
                } else {
                    sensitivityModel.setLevel(1);
                    sensitivityModel.setPredictClass("DA");
                }

            } else if (contentModel.isPassedEO()) {
                //System.out.println("CONTENT: Possible Email Dump!");
                sensitivityModel.setPredictClass("EO");
                int email_count = EOCounter(post);
                if (email_count < 50) {
                    sensitivityModel.setLevel(1);
                } else {
                    sensitivityModel.setLevel(2);
                }
            }
        } else if (contentModel.isPassedEC()) {
            sensitivityModel.setPredictClass("EC");
            ArrayList<String> ECList = new ArrayList<String>(Arrays.asList("CONFIDENTIAL", "secret", "do not disclose"));
            int ecCount = 0;
            for (String i : ECList) {
                if (post.contains(i)) {
                    ecCount++;
                }
            }
            if (ecCount > 0) {
                sensitivityModel.setLevel(3);
            } else {
                sensitivityModel.setLevel(1);
            }

        }
        if (evidenceModel.isEvidenceFound() && !contentModel.isContentFound()) {
            sensitivityModel.setLevel(3);
        }
        if (!evidenceModel.isEvidenceFound() && !contentModel.isContentFound()) {
            sensitivityModel.setLevel(0);
        }

        return sensitivityModel;
        //System.err.println("Sensitivity : " + sensitivityLabel);

    }


    //******************************** EO related functions ******************************************** //
    public int EOCounter(String post) {

        Pattern emailPattern = Pattern.compile("(([a-zA-Z]|[0-9])|([-]|[_]|[.]))+[@](([a-zA-Z0-9])|([-])){2,63}([.]((([a-zA-Z0-9])|([-])){2,63})){1,4}");

        Matcher matcherEO = emailPattern.matcher(post);
        int EO_Count = getMatchingCount(matcherEO);
        return EO_Count;
    }

    //******************************** CC related functions ******************************************** //
    public int extractCCNumberCount(String post) {

        ccCardPattern = Pattern.compile("[2-6][0-9]{3}([ -]?)[0-9]{4}([ -]?)[0-9]{4}([ -]?)[0-9]{3,4}([ -]?)[0-9]{0,3}[?^a-zA-Z]?");

        Matcher matcherCC = ccCardPattern.matcher(post);
        int CC_Count = getMatchingCount(matcherCC);
        return CC_Count;
    }

    public int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("no-no"));
    }
}



