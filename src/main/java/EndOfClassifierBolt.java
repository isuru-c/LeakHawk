import classifiers.EvidenceModel;
import classifiers.ContentModel;
import data.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Isuru Chandima on 7/28/17.
 */
public class EndOfClassifierBolt extends BaseRichBolt {

    OutputCollector collector;
    static int count = 0;
    private StringBuilder sensitivityLabel= new StringBuilder();
    private int creditCardNumberCount;
    private int URLratio;
    private int email_hash_count;
    Pattern ccCardPattern;
    EvidenceModel evidenceModel;
    ContentModel contentModel;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

    }

    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);
        evidenceModel = post.getEvidenceModel();
        contentModel = post.getContentModel();

        predictSensitivity(post.getPostText());

        System.out.println("Sensitivity: "+ sensitivityLabel);

        System.out.println("\nKey: " + post.getKey() + "\nDate: " + post.getDate() + "\nUser: " + post.getUser() + "\nTitle: " + post.getTitle() + "\n" + post.getPostText() + "\nCount: " + count++);

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

    public StringBuilder getSensitivityLabel() {
        return sensitivityLabel;
    }

    public void setSensitivityLabel(StringBuilder sensitivityLabel) {
        this.sensitivityLabel = sensitivityLabel;
    }

    public void predictSensitivity(String post){

        if ((contentModel.isContentFound() && evidenceModel.isEvidenceFound()) || contentModel.isContentFound()){
            sensitivityLabel.append("CRITICAL-Evidence+Content");

            if(contentModel.isPassedCC()){
                setCreditCardNumberCount(extractCCNumberCount(post));

                if ((creditCardNumberCount < 5) ){
                    sensitivityLabel.append(" /LOW-CC");
                }
                else if( (creditCardNumberCount < 20) && (creditCardNumberCount > 5) ){
                    sensitivityLabel.append("HIGH-CC");
                }
                else if( creditCardNumberCount > 20 ){
                    sensitivityLabel.append("CRITICAL-CC");
                }
            }
            else if( contentModel.isPassedPK()){
                //System.out.println("CONTENT: Possible Private Key Compromise!");
                sensitivityLabel.append("CRITICAL-PK");
            }
            else if( contentModel.isPassedCF() ) {

                if(post.contains("enable password")){
                    sensitivityLabel.append("CRITICAL-CF");
                }
                else {
                    sensitivityLabel.append("LOW - CF");
                }
            }
            else if( contentModel.isPassedDB() ){
                  //System.out.println("CONTENT: Possible Database Dump!");
                sensitivityLabel.append("HIGH-DB");

                // if evidence passed, escalated to CRITICAL

                //weak hashes based on https://en.wikipedia.org/wiki/Hash_function_security_summary
//                if(presenseOfSensitiveData("/home/nalinda/oct/leakhawk-app/predictor/DB_sensitiveData.sh")){
//                    sensitivityLabel = "CRITICAL-DB";
//                }
            }
            else if( contentModel.isPassedDA() ){
                //            //System.out.println("CONTENT: Possible DNS attack!");
                //
                //            setDomainCount( Integer.parseInt( DAcounter() ));
                //            if (domainCount < 10){
                //                sensitivityLabel = "CRITICAL-DA";
                //            }
                //
                //            if (domainCount >= 10){
                //                sensitivityLabel = "CRITICAL-DA-l";
                //            }

                        }
        }
        if(evidenceModel.isEvidenceFound() && !contentModel.isContentFound()){
            sensitivityLabel.append("CRITICAL-Evidence only");
        }

        setSensitivityLabel(sensitivityLabel);
        //System.err.println("Sensitivity : " + sensitivityLabel);

    }

    //******************************** CC related functions ******************************************** //
    public int extractCCNumberCount(String post){

        ccCardPattern = Pattern.compile("[2-6][0-9]{3}([ -]?)[0-9]{4}([ -]?)[0-9]{4}([ -]?)[0-9]{3,4}([ -]?)[0-9]{0,3}[?^a-zA-Z]?");

        Matcher matcherCC = ccCardPattern.matcher(post);
        int CC_Count= getMatchingCount(matcherCC);
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



