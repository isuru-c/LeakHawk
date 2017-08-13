import classifiers.EvidenceModel;
import data.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class EvidenceClassifierBolt extends BaseRichBolt {

    OutputCollector collector;

    ArrayList<String> keyWordList1;
    ArrayList<String> keyWordList2;
    ArrayList<String> keyWordList3;
    ArrayList<String> keyWordList4;
    ArrayList<String> keyWordList5;
    ArrayList<String> keyWordList6;
    ArrayList<String> keyWordList7;
    ArrayList<String> keyWordList8;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

        keyWordList1 = new ArrayList(Arrays.asList("Hacked", "leaked by", "Pwned by", "Doxed", "Ow3ned", "pawned by", "Server Rootéd", "#opSriLanka", "#OPlanka", "#anonymous", "Private key", "Password leak", "password dump", "credential leak", "credential dump", "Credit card", "Card dump ", " cc dump ", " credit_card", "card_dump", "working_card", "cc_dump", "skimmed", "card_hack", "sited hacked by", "websited hacked by", "website hacked by", "site hacked by", "websited hacked", "domain hack", "defaced", "leaked by", "site deface", "mass deface", "database dump", "database dumped", "db dumped", "db_dump", "db leak", "data base dump", "data base leak", "database hack", "db hack", "login dump", "DNS LeAkEd", "DNS fuck3d", "zone transfer", "DNS Enumeration", "Enumeration Attack", "cache snooping", "cache poisoning", "email hack", "emails hack", "emails leak,email leak", "email dump", "emails dump", "email dumps", "email-list", "leaked email,leaked emails", "email_hack"));
        keyWordList2 = new ArrayList(Arrays.asList("dns-brute", "dnsrecon", "fierce", "Dnsdict6", "axfr", "SQLmap"));
        keyWordList3 = new ArrayList(Arrays.asList("SQL_Injection", "SQLi", "SQL-i", "Blind SQL-i"));
        keyWordList4 = new ArrayList(Arrays.asList("UGLegion", "RetrOHacK", "Anonymous", "AnonSec", "AnonGhost", "ANONYMOUSSRILANKA", "W3BD3F4C3R", "SLCYBERARMY", "DAVYJONES", "BLACKHATANON", "ANUARLINUX", "UGLEGION", "HUSSEIN98D", "We_Are_Anonymous", "We_do_not_Forget", "We_do_not_Forgive", "Laughing_at_your_security_since_2012", "AnonGhost_is_Everywhere"));
        keyWordList5 = new ArrayList(Arrays.asList("Hacked", "leaked by", "Pwned by", "Doxed", "Ow3ned", "pawned by", "Server Rootéd", "#opSriLanka", "#OPlanka", "#anonymous", "Private key", "Password leak", "password dump", "credential leak", "credential dump", "Credit card", "Card dump ", " cc dump ", " credit_card", "card_dump", "working_card", "cc_dump", "skimmed", "card_hack", "sited hacked by", "websited hacked by", "website hacked by", "site hacked by", "websited hacked", "domain hack", "defaced", "leaked by", "site deface", "mass deface", "database dump", "database dumped", "db dumped", "db dump", "db leak", "data base dump", "data base leak", "database hack", "db hack", "login dump", "DNS LeAkEd", "DNS fuck3d", "zone transfer", "DNS Enumeration", "Enumeration Attack", "cache snooping", "cache poisoning", "email hack", "emails hack", "emails leak,email leak", "email dump", "emails dump", "email dumps", "email-list", "leaked email,leaked emails", "email_hack"));
        keyWordList6 = new ArrayList(Arrays.asList("dns-brute", "dnsrecon", "fierce", "Dnsdict6", "axfr", "SQLmap"));
        keyWordList7 = new ArrayList(Arrays.asList("SQL Injection", "SQLi", "SQL-i", "Blind SQL-i"));
        keyWordList8 = new ArrayList(Arrays.asList("UGLegion", "RetrOHacK", "Anonymous", "AnonSec", "AnonGhost", "ANONYMOUSSRILANKA", "W3BD3F4C3R", "SLCYBERARMY", "DAVYJONES", "BLACKHATANON", "ANUARLINUX", "UGLEGION", "HUSSEIN98D", "We Are Anonymous", "We do not Forget, We do not Forgive", "Laughing at your security since 2012", "AnonGhost is Everywhere"));

    }

    public void execute(Tuple tuple) {

        Post post = (Post)tuple.getValue(0);

        EvidenceModel evidenceModel = new EvidenceModel();
        post.setEvidenceModel(evidenceModel);

        Boolean evidenceFound = isPassedEvidenceClassifier(post.getUser(), post.getTitle(), post.getPostText(), evidenceModel);

        evidenceModel.setEvidenceFound(evidenceFound);

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

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
