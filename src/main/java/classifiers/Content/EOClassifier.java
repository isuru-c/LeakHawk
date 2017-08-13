package classifiers.Content;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
public class EOClassifier extends ContentClassifier {

    Pattern relatedPattern1;
    Pattern relatedPattern2;

    Pattern emailPattern;


    public EOClassifier() {


        relatedPattern1 = Pattern.compile("\\b" + "\"email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack\"" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = Pattern.compile("\\b" + "leaked by|Emails LeakeD|domains hacked|leaked email list|email list leaked|leaked emails|leak of|email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack" + "\\b", Pattern.CASE_INSENSITIVE);
        emailPattern = Pattern.compile("(([a-zA-Z]|[0-9])|([-]|[_]|[.]))+[@](([a-zA-Z0-9])|([-])){2,63}([.]((([a-zA-Z0-9])|([-])){2,63})){1,4}");


    }

    @Override
    public String createARFF(String text, String title) {
        String feature_list = "";


        Matcher matcherEO = relatedPattern1.matcher(title);
        feature_list += getMatchingCount(matcherEO) + ",";

        matcherEO = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherEO) + ",";


        matcherEO = emailPattern.matcher(text);
        int emailCount = getMatchingCount(matcherEO);
        feature_list += emailCount + ",";

        int wordCount= text.replace('[', ' ').replace('*', ' ').replace(']', ' ').replace(',', ' ').replace('/', ' ').replace(':', ' ').split("\\s+").length;
        feature_list += wordCount + ",";

        double rate = ((double) emailCount/wordCount)*100;
        if(rate>89){
            feature_list += 1 + ",";
        }else {
            feature_list += 0 + ",";
        }

        feature_list += "?";
        return headingEO + feature_list;
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

            RandomForest tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/EO.model");
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
