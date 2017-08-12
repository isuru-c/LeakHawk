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
    private String sensitivity;
    private String sensitivityLabel= "Sensitivity: ";
    private int creditCardNumberCount;
    private int URLratio;
    private int email_hash_count;
    Pattern ccCardPattern;
//    SensitivityPredictor sensitivityPredictor = new SensitivityPredictor();

    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

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

        System.out.println("************************Synthesiser*********************");
        System.out.println("\nKey: " + key + "\nDate: " + date + "\nUser: " + user + "\nTitle: " + title + "\n" + post + "\nCount: " + count++);

        predictSensitivity(post);
        setSensitivity(getSensitivityLabel());
        System.out.println(sensitivity);
        collector.ack(tuple);
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("no-no"));
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

    public String getSensitivityLabel() {
        return sensitivityLabel;
    }

    public void setSensitivityLabel(String sensitivityLabel) {
        this.sensitivityLabel = sensitivityLabel;
    }

    public void predictSensitivity(String post){

        EvidenceClassifierBolt evidenceClassifier= new EvidenceClassifierBolt();
        ContentClassifierBolt contentClassifier= new ContentClassifierBolt();

        if (contentClassifier.isContentClassifierPassed() && evidenceClassifier.isEvidenceClassifierPassed()){
            sensitivityLabel.concat("CRITICAL-Evidence+Content");
        }

        if(contentClassifier.isContentClassifierPassed()) {

            //        if( contentClassifier.isCCPassed() ){
            //System.out.println("CONTENT: Possible Credit Card Breach");

            setCreditCardNumberCount(extractCCNumberCount(post));

            if ((creditCardNumberCount < 5)) {
                sensitivityLabel.concat("LOW-CC");
            }
            if ((creditCardNumberCount < 20) && (creditCardNumberCount > 5)) {
                sensitivityLabel.concat("HIGH-CC");
            }
            if (creditCardNumberCount > 20) {
                sensitivityLabel.concat("CRITICAL-CC");
            }
            //        }


//        if( classifierResult.isPKPassed() ){
//            //System.out.println("CONTENT: Possible Private Key Compromise!");
//            sensitivityLabel = "CRITICAL-PK";
//        }
//
//
//        if( classifierResult.isWDPassed() ){
//
//            setURLratio(Integer.parseInt( extractURLratio() ));
//            //System.out.println("CONTENT: Possible Website defacement incident!");
//
//            if( (URLratio > 0) && (URLratio <70)){
//                sensitivityLabel = "HIGH";
//            }
//
//            if( URLratio > 70){
//                sensitivityLabel = "CRITICAL-WD";
//            }
//        }
//
//
//        if( classifierResult.isCFPassed() ){
//            //System.out.println("CONTENT: Possible Configuration file exposure!");
//
//            if(presenseOfSensitiveData("/home/nalinda/oct/leakhawk-app/predictor/CF_sensitiveData.sh")){
//                sensitivityLabel = "CRITICAL-CF";
//                entry.getSensitivityResultMsgList().add( "Possible Plaintext passwords found!" );
//                //System.out.println("Possible Plaintext passwords found!");
//            }
//        }
//
//
//        if( classifierResult.isDBPassed() ){
//            //System.out.println("CONTENT: Possible Database Dump!");
//            sensitivityLabel = "HIGH-DB";
//
//            // if evidence passed, escalated to CRITICAL
//
//            //weak hashes based on https://en.wikipedia.org/wiki/Hash_function_security_summary
//            if(presenseOfSensitiveData("/home/nalinda/oct/leakhawk-app/predictor/DB_sensitiveData.sh")){
//                sensitivityLabel = "CRITICAL-DB";
//            }
//        }
//
//
//        if( classifierResult.isUCPassed() ){
//            //System.out.println("CONTENT: Possible Credentials Dump!");
//            setEmail_hash_count( Integer.parseInt( UCcounter() ));
//
//            if (email_hash_count > 30){
//                sensitivityLabel = "CRITICAL-UC";
//            }
//        }
//
//
//        if( classifierResult.isDAPassed() ){
//            //System.out.println("CONTENT: Possible DNS attack!");
//
//
//            setDomainCount( Integer.parseInt( DAcounter() ));
//            if (domainCount < 10){
//                sensitivityLabel = "CRITICAL-DA";
//            }
//
//            if (domainCount >= 10){
//                sensitivityLabel = "CRITICAL-DA-l";
//            }
//
//        }
//
//
//        if( classifierResult.isEOPassed() ){
//            //System.out.println("CONTENT: Possible Email Dump!");
//
//            setEmail_count( Integer.parseInt( EOcounter() ));
//            if (email_count < 50){
//                sensitivityLabel = "LOW-EO";
//            }
//
//            if (email_count >= 50){
//                sensitivityLabel = "HIGH-EO";
//            }
//        }
//
//
//        if( classifierResult.isECPassed() ){
//            //System.out.println("CONTENT: Possible Email conversation!");
//
//            if(presenseOfSensitiveData("/home/nalinda/oct/leakhawk-app/predictor/EC_sensitiveData.sh")){
//                sensitivityLabel = "CRITICAL-EC";
//            }
//        }
            //else if
        }
        else {
//            if (evidenceClassifier.isEvidenceClassifierPassed() && !contentClassifier.isContentClassifierPassed()){
                sensitivityLabel = "CRITICAL-Evidence only";
//            }
        }


        setSensitivityLabel(sensitivityLabel);
        //System.err.println("Sensitivity : " + sensitivityLabel);

    }

    //******************************** CC related functions ******************************************** //
    public int extractCCNumberCount(String post){

        ccCardPattern = Pattern.compile("[2-6][0-9]{3}([ -]?)[0-9]{4}([ -]?)[0-9]{4}([ -]?)[0-9]{3,4}([ -]?)[0-9]{0,3}[?^a-zA-Z]?");

        Matcher matcherCC = ccCardPattern.matcher(post);
        int CC_Count= getMatchingCount(matcherCC);
        System.out.println("ExtractCCCount: "+CC_Count);
        return CC_Count;
    }

    int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

}
