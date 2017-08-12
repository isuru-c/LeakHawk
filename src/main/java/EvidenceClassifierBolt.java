import org.apache.logging.log4j.util.Strings;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class EvidenceClassifierBolt extends BaseRichBolt{

    OutputCollector collector;

    public boolean isEvidenceClassifierPassed() {
        return evidenceClassifierPassed;
    }

    public void setEvidenceClassifierPassed(boolean evidenceClassifierPassed) {
        this.evidenceClassifierPassed = evidenceClassifierPassed;
    }

    public boolean evidenceClassifierPassed = false;//needed for sensitivity prediction
//    private boolean isClassifier1Passed= false;
//    private boolean isClassifier2Passed= false;
//    private boolean isClassifier3Passed= false;
//    private boolean isClassifier4Passed= false;
//    private boolean isClassifier5Passed= false;
//    private boolean isClassifier6Passed= false;
//    private boolean isClassifier7Passed= false;
//    private boolean isClassifier8Passed= false;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
    }

    public void execute(Tuple tuple) {

        String type = tuple.getString(0);
        String key = tuple.getString(1);
        String date = tuple.getString(2);
        String user = tuple.getString(3);
        String title = tuple.getString(4);
        String syntax = tuple.getString(5);
        String post = tuple.getString(6);

        System.out.println("*******************Evidence Classifier********************************");

        //if evidence classifier is passed forward the data to next bolt(Content classifier)
        if(isPassedEvidenceClassifier(user,title,post)) {
            System.out.println("Passed evidence classifier: "+post);
            setEvidenceClassifierPassed(true);
            collector.emit(tuple, new Values(type, key, date, user, title, syntax, post));
        }

        collector.ack(tuple);
    }

    //check whether evidence classifier is passed or not
    private boolean isPassedEvidenceClassifier(String user, String title, String post) {

        //Arrays for each heuristic checkpoint
        ArrayList<String> keyWordList1 = new ArrayList(Arrays.asList("Hacked", "leaked by", "Pwned by", "Doxed", "Ow3ned", "pawned by", "Server Rootéd", "#opSriLanka", "#OPlanka", "#anonymous", "Private key", "Password leak", "password dump", "credential leak", "credential dump", "Credit card", "Card dump ", " cc dump ", " credit_card", "card_dump", "working_card", "cc_dump", "skimmed", "card_hack", "sited hacked by", "websited hacked by", "website hacked by", "site hacked by", "websited hacked", "domain hack", "defaced", "leaked by", "site deface", "mass deface", "database dump", "database dumped", "db dumped", "db_dump", "db leak", "data base dump", "data base leak", "database hack", "db hack", "login dump", "DNS LeAkEd", "DNS fuck3d", "zone transfer", "DNS Enumeration", "Enumeration Attack", "cache snooping", "cache poisoning", "email hack", "emails hack", "emails leak,email leak", "email dump", "emails dump", "email dumps", "email-list", "leaked email,leaked emails", "email_hack"));
        ArrayList<String> keyWordList2 = new ArrayList(Arrays.asList("dns-brute", "dnsrecon", "fierce", "Dnsdict6", "axfr", "SQLmap"));
        ArrayList<String> keyWordList3 = new ArrayList(Arrays.asList("SQL_Injection", "SQLi", "SQL-i", "Blind SQL-i"));
        ArrayList<String> keyWordList4 = new ArrayList(Arrays.asList("UGLegion", "RetrOHacK", "Anonymous", "AnonSec", "AnonGhost", "ANONYMOUSSRILANKA", "W3BD3F4C3R", "SLCYBERARMY", "DAVYJONES", "BLACKHATANON", "ANUARLINUX", "UGLEGION", "HUSSEIN98D", "We_Are_Anonymous", "We_do_not_Forget", "We_do_not_Forgive", "Laughing_at_your_security_since_2012", "AnonGhost_is_Everywhere"));
        ArrayList<String> keyWordList5 = new ArrayList(Arrays.asList("Hacked", "leaked by", "Pwned by", "Doxed", "Ow3ned", "pawned by", "Server Rootéd", "#opSriLanka", "#OPlanka", "#anonymous", "Private key", "Password leak", "password dump", "credential leak", "credential dump", "Credit card", "Card dump ", " cc dump ", " credit_card", "card_dump", "working_card", "cc_dump", "skimmed", "card_hack", "sited hacked by", "websited hacked by", "website hacked by", "site hacked by", "websited hacked", "domain hack", "defaced", "leaked by", "site deface", "mass deface", "database dump", "database dumped", "db dumped", "db dump", "db leak", "data base dump", "data base leak", "database hack", "db hack", "login dump", "DNS LeAkEd", "DNS fuck3d", "zone transfer", "DNS Enumeration", "Enumeration Attack", "cache snooping", "cache poisoning", "email hack", "emails hack", "emails leak,email leak", "email dump", "emails dump", "email dumps", "email-list", "leaked email,leaked emails", "email_hack"));
        ArrayList<String> keyWordList6 = new ArrayList(Arrays.asList("dns-brute", "dnsrecon", "fierce", "Dnsdict6", "axfr", "SQLmap"));
        ArrayList<String> keyWordList7 = new ArrayList(Arrays.asList("SQL Injection", "SQLi", "SQL-i", "Blind SQL-i"));
        ArrayList<String> keyWordList8 = new ArrayList(Arrays.asList("UGLegion", "RetrOHacK", "Anonymous", "AnonSec", "AnonGhost", "ANONYMOUSSRILANKA", "W3BD3F4C3R", "SLCYBERARMY", "DAVYJONES", "BLACKHATANON", "ANUARLINUX", "UGLEGION", "HUSSEIN98D", "We Are Anonymous", "We do not Forget, We do not Forgive", "Laughing at your security since 2012", "AnonGhost is Everywhere"));

        //#U1-USER: Does the user, seems suspicious?
        //need to compare with the database - percentage

        //#E1 	SUBJECT:Is there any evidence of a hacking attack on the subject?
        for (String i:keyWordList1) {
            if (title.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier1Passed=true;
                return true;
            }
        }

        //#E2 	SUBJECT:Are there any signs of usage of a security tool?
        for (String i:keyWordList2) {
            if (title.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier2Passed=true;
                return true;
            }
        }

        //#E3 	SUBJECT:Are there any signs of security vulnerability?
        for (String i:keyWordList3) {
            if (title.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier3Passed=true;
                return true;
            }
        }

        //#E4 	SUBJECT:Evidence of a Hacker involvement/Hacktivist movement?
        for (String i:keyWordList4) {
            if (title.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier4Passed=true;
                return true;
            }
        }

        //#E5 	BODY:	Is there any evidence of a hacking attack in the body text?
        for (String i:keyWordList5) {
            if (post.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier5Passed=true;
                return true;
            }
        }

        //#E6 	BODY:	Are there any signs of usage of a security tool in the body text?
        for (String i:keyWordList6) {
            if (post.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier6Passed=true;
                return true;
            }
        }

        //#E7	BODY:	Are there any signs of security vulnerability in the body text?
        for (String i:keyWordList7) {
            if (post.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier7Passed=true;
                return true;
            }
        }

        //#E8	BODY:	Are there any signs of security vulnerability in the body text?
        for (String i:keyWordList8) {
            if (post.toLowerCase().contains(i.toLowerCase())) {
//                isClassifier8Passed=true;
                return true;
            }
        }

        return false;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("type", "key", "date", "user", "title", "syntax", "post"));
    }
}
