package classifiers.Content;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
public class DAClassifier extends ContentClassifier {

    ArrayList<Pattern> unigramPatternList;
    ArrayList<Pattern> bigramPatternList;
    ArrayList<Pattern> trigramPatternList;

    Pattern relatedPattern1;
    Pattern relatedPattern2;
    Pattern relatedPattern3;
    Pattern relatedPattern4;
    Pattern relatedPattern5;
    Pattern relatedPattern6;
    Pattern relatedPattern7;

    public DAClassifier() {
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("MX|NS|PTR|CNAME|SOA");
        unigramList.add("dns|record|host");
        unigramList.add("INTO");
        unigramList.add("snoop|axfr|brute|poisoning");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("43200 IN|10800 IN|86400 IN|3600 IN");
        bigramList.add("IN A|IN MX|IN NS|IN CNAME");
        bigramList.add("no PTR");
        bigramList.add("hostnames found");
        bigramList.add("zone transfer");
        bigramList.add("MX 10|MX 20|MX 30|MX 40|MX 50|MX 60");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("transfer not allowed");
        trigramList.add("Trying zone transfer");


        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
                unigramPatternList.add(Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            trigramPatternList.add(Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        relatedPattern1 = Pattern.compile("\\b" + "dns-brute|dnsrecon|fierce|tsunami|Dnsdict6|axfr" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = Pattern.compile("dns-brute|dnsrecon|fierce|tsunami|Dnsdict6|axfr", Pattern.CASE_INSENSITIVE);
        relatedPattern3 = Pattern.compile("\\b" + "DNS LeAkEd|DNS fuck3d|zone transfer|DNS_Enumeration|Enumeration_Attack" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern4 = Pattern.compile("DNS LeAkEd|DNS fuck3d|zone transfer|DNS_Enumeration|Enumeration_Attack", Pattern.CASE_INSENSITIVE);
        relatedPattern5 = Pattern.compile("\\b" + "DNS Enumeration Attack|DNS enumeration|zone transfer|misconfigured DNS|DNS Cache Snooping" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern6 = Pattern.compile("DNS Enumeration Attack|DNS enumeration|zone transfer|misconfigured DNS|DNS Cache Snooping", Pattern.CASE_INSENSITIVE);
        relatedPattern7 = Pattern.compile("\\[\\*\\]", Pattern.CASE_INSENSITIVE);
    }


    @Override
    public String createARFF(String text,String title) {
        String feature_list = "";

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

        Matcher matcher = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern2.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern3.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern4.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern5.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern6.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern7.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        feature_list += "?";
        return headingDA+feature_list;
    }

    @Override
    public boolean classify(String text,String title) {
        try{
            // convert String into InputStream
            String result = createARFF(text,title);
            InputStream is = new ByteArrayInputStream(result.getBytes());

            // read it with BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // BufferedReader reader = new BufferedReader
            Instances unlabeled = new Instances(reader);
            reader.close();
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // create copy
            Instances labeled = new Instances(unlabeled);

            RandomForest tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/DA.model");
            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
//        System.out.println("Result:"+pred);

            if(pred>=0.5){
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
