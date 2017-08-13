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
public class CFClassifier extends ContentClassifier{
    Pattern cfSymbalPattern;
    ArrayList<Pattern> unigramPatternList;
    ArrayList<Pattern> bigramPatternList;
    ArrayList<Pattern> trigramPatternList;

    Pattern digitPattern;
    Pattern alphaPattern;
    Pattern alphDigitPattern;

    public CFClassifier() {
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("ip");
        unigramList.add("cisco");
        unigramList.add("password-encryption");
        unigramList.add("spanning-tree");
        unigramList.add("domain-lookup");
        unigramList.add("serial0/0/0");
        unigramList.add("access-list");


        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("interface FastEthernet[0-9]|interface Serial[0-9]");
        bigramList.add("speed auto|duplex auto");
        bigramList.add("0 line");
        bigramList.add("line vty|line aux|line con\"");
        bigramList.add("service password");
        bigramList.add("ip address");
        bigramList.add("ip route");
        bigramList.add("banner motd");
        bigramList.add("no service");
        bigramList.add("clock rate");
        bigramList.add("ip cef|ipv6 cef");
        bigramList.add("service password-encryption");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("line vty 0|line con 0|line aux 0");
        trigramList.add("no ip address");
        trigramList.add("no ipv6 cef");
        trigramList.add("switchport access vlan");


        cfSymbalPattern = Pattern.compile("!");

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

        Matcher matcherCF = cfSymbalPattern.matcher(text);
        feature_list += getMatchingCount(matcherCF) + ",";

        feature_list += ",?";
        return headingCF+feature_list;
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

            RandomForest tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/CF.model");
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

