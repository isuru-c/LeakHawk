package Classifiers.Content;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
public class CFClassifier {
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
            if (word.equals("CC")) {
                unigramPatternList.add(Pattern.compile("\\b" + word + "\\b"));
            } else {
                unigramPatternList.add(Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
            }
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


    public String classify(String text) {
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

        Matcher matcherCC = cfSymbalPattern.matcher(text);
        feature_list += getMatchingCount(matcherCC) + ",";

        feature_list += ",?";
        return feature_list;
    }

    int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

}

