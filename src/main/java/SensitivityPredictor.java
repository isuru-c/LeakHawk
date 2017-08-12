import org.apache.storm.tuple.Tuple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SensitivityPredictor {

    private String sensitivityLabel;
    private int creditCardNumberCount;
    private int URLratio;
    private int email_hash_count;

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



    public void predictSensitivity(Tuple tuple){

        String post = tuple.getString(6);
        EvidenceClassifierBolt evidenceClassifier= new EvidenceClassifierBolt();
        ContentClassifierBolt contentClassifier= new ContentClassifierBolt();

//        if( contentClassifier.isCCPassed() ){
//            //System.out.println("CONTENT: Possible Credit Card Breach");
//
//            setCreditCardNumberCount(Integer.parseInt( extractCCNumberCount(post) ) );
//
//            if ((creditCardNumberCount < 5) ){
//                sensitivityLabel = "LOW-CC";
//            }
//
//            if( (creditCardNumberCount < 20) && (creditCardNumberCount > 5) ){
//                sensitivityLabel = "HIGH-CC";
//            }
//
////            if((creditCardNumberCount > 0) && presenseOfSensitiveData("/home/nalinda/oct/leakhawk-app/predictor/CC_sensitiveData.sh")){
////                sensitivityLabel = "CRITICAL-CC";
//////                entry.getSensitivityResultMsgList().add( "Possible sensitive authentication data found!" );
////                //System.out.println("Possible sensitive authentication data found!");
////            }
//
//            if( creditCardNumberCount > 20 ){
//                sensitivityLabel = "CRITICAL-CC";
//            }
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

        if (evidenceClassifier.isEvidenceClassifierPassed()){
            sensitivityLabel = "CRITICAL-Evidence only";
        }

        if (contentClassifier.isContentClassifierPassed() && evidenceClassifier.isEvidenceClassifierPassed()){
            sensitivityLabel = "CRITICAL-Evidence+Content";
        }

        setSensitivityLabel(sensitivityLabel);
        //System.err.println("Sensitivity : " + sensitivityLabel);

    }

    //******************************** CC related functions ******************************************** //
//    public String extractCCNumberCount(String post){
//
//        StringBuilder sb = new StringBuilder();
//        try {
//
//            ProcessBuilder pbVal = new ProcessBuilder("/bin/bash", "/home/nalinda/oct/leakhawk-app/predictor/CC_counter.sh", FileManager.sensitiveFilePath + getEntry().getEntryFileName());
//            final Process processVal = pbVal.start();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(processVal.getInputStream()));
//            PrintWriter pw = new PrintWriter(processVal.getOutputStream());
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                //				System.out.println(line+"\n");
//                sb.append( line );
//                pw.flush();
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//
//        //System.out.println("CC count:"+sb.toString());
//        return sb.toString();
//    }



}
