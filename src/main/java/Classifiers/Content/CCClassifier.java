package Classifiers.Content;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
public class CCClassifier {
    Pattern ccCardPattern;
    Pattern patterncc1;
    ArrayList<Pattern> unigramPatternList;
    ArrayList<Pattern> bigramPatternList;
    ArrayList<Pattern> trigramPatternList;
    Pattern relatedTerms1Pattern;
    Pattern relatedTerms2Pattern;
    Pattern relatedTerms3Pattern;
    Pattern relatedTerms4Pattern;
    Pattern relatedTerms5Pattern;
    Pattern relatedTerms6Pattern;
    Pattern digitPattern;
    Pattern alphaPattern;
    Pattern alphDigitPattern;
    String heading = "@relation CC\n" +
            "\n" +
            "@attribute $CC1 numeric\n" +
            "@attribute $CC2 numeric\n" +
            "@attribute $CC3 numeric\n" +
            "@attribute $CC4 numeric\n" +
            "@attribute $CC5 numeric\n" +
            "@attribute $CC6 numeric\n" +
            "@attribute $CC7 numeric\n" +
            "@attribute $CC8 numeric\n" +
            "@attribute $CC9 numeric\n" +
            "@attribute $CC10 numeric\n" +
            "@attribute $CC11 numeric\n" +
            "@attribute $CC12 numeric\n" +
            "@attribute $CC13 numeric\n" +
            "@attribute $CC14 numeric\n" +
            "@attribute $CC15 numeric\n" +
            "@attribute $CC16 numeric\n" +
            "@attribute $CC17 numeric\n" +
            "@attribute $CC18 numeric\n" +
            "@attribute $CC19 numeric\n" +
            "@attribute $CC20 numeric\n" +
            "@attribute $CC21 numeric\n" +
            "@attribute $CC22 numeric\n" +
            "@attribute $CC23 numeric\n" +
            "@attribute $CC24 numeric\n" +
            "@attribute $CC25 numeric\n" +
            "@attribute $CC26 numeric\n" +
            "@attribute $CC27 numeric\n" +
            "@attribute $CC28 numeric\n" +
            "@attribute $CC29 numeric\n" +
            "@attribute $CC30 numeric\n" +
            "@attribute $CC31 numeric\n" +
            "@attribute $CC32 numeric\n" +
            "@attribute $CC33 numeric\n" +
            "@attribute $CC34 numeric\n" +
            "@attribute $CC35 numeric\n" +
            "@attribute #N numeric\n" +
            "@attribute #L numeric\n" +
            "@attribute #A numeric\n" +
            "@attribute #NP numeric\n" +
            "@attribute #CP numeric\n" +
            "@attribute @@class@@ {CC,non}\n" +
            "\n" +
            "@data\n";

    public CCClassifier() {
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("card");
        unigramList.add("number");
        unigramList.add("credit");
        unigramList.add("expiration");
        unigramList.add("CC");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("Name On");
        bigramList.add("card number");
        bigramList.add("credit card");
        bigramList.add("Expiration Date | exp date");
        bigramList.add("Maiden Name");
        bigramList.add("zip code");
        bigramList.add("account number");
        bigramList.add("card type");
        bigramList.add("card information");
        bigramList.add("CC number");
        bigramList.add("card hack");
        bigramList.add("ATM pin");
        bigramList.add("account information");
        bigramList.add("mother maiden|mothers maiden|mother's maiden");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("Date of Birth");
        trigramList.add("Credit Card Number");
        trigramList.add("Credit Card Information");
        trigramList.add("name on card");
        trigramList.add("card verification code");
        trigramList.add("card verification number");
        trigramList.add("bank account number");
        trigramList.add("visa card number");

        ccCardPattern = Pattern.compile("[2-6][0-9]{3}([ -]?)[0-9]{4}([ -]?)[0-9]{4}([ -]?)[0-9]{3,4}([ -]?)[0-9]{0,3}[?^a-zA-Z]?");
//        patterncc1 = Pattern.compile("\\bcard\\b");

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

        relatedTerms1Pattern = Pattern.compile("Expiry Date|Expire|Exp.Date|Expiration|Exp. month|Exp. Years|expyear|expmonth|(exp)|ExpDate|ExpD[m/y]|Date D'expiration", Pattern.CASE_INSENSITIVE);
        relatedTerms2Pattern = Pattern.compile("CVV |card verification number| CVV2|CCV2|CVC|CVC2|verification code|CID|CAV2", Pattern.CASE_INSENSITIVE);
        relatedTerms3Pattern = Pattern.compile("credit_card|card_dump|working_card|cc_dump|skimmed|card_hack", Pattern.CASE_INSENSITIVE);
        relatedTerms4Pattern = Pattern.compile("CVV2|CVV|CVC2|CAV2");
        relatedTerms5Pattern = Pattern.compile("VISA|Mastercard|JCB|AMEX|american express|Discover|Diners Club", Pattern.CASE_INSENSITIVE);
        relatedTerms6Pattern = Pattern.compile("Debit or credit card number|Credit Card Number|credit_number|Card Number|cardnum|Primary Account Number|CC Number", Pattern.CASE_INSENSITIVE);

        digitPattern = Pattern.compile("(0|[1-9][0-9]*)");
        alphaPattern = Pattern.compile("[a-zA-Z]");
        alphDigitPattern = Pattern.compile("[a-zA-Z0-9]");


    }


    public String createARFF(String text) {
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

        Matcher matcherCC = ccCardPattern.matcher(text);
        feature_list += getMatchingCount(matcherCC) + ",";

        int relatedTermsSum = 0;


        Matcher matcher = relatedTerms1Pattern.matcher(text);
        int matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";
        relatedTermsSum += matchingCount;

        matcher = relatedTerms2Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";
        relatedTermsSum += matchingCount;


        matcher = relatedTerms3Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";
        relatedTermsSum += matchingCount;


        matcher = relatedTerms4Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";
        relatedTermsSum += matchingCount;

        matcher = relatedTerms5Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";
        relatedTermsSum += matchingCount;


        matcher = relatedTerms6Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";
        relatedTermsSum += matchingCount;


        feature_list += relatedTermsSum + ",";

        Matcher matcher1 = digitPattern.matcher(text);
        String digitStr = "";
        while (matcher1.find()) {
            digitStr += matcher1.group();
        }


        Matcher matcher2 = alphaPattern.matcher(text);
        String alphaStr = "";
        while (matcher2.find()) {
            alphaStr += matcher2.group();
        }

        Matcher matcher3 = alphDigitPattern.matcher(text);
        String alphDigitStr = "";
        while (matcher3.find()) {
            alphDigitStr += matcher3.group();
        }

        int a = digitStr.length();
        int b = alphaStr.length();
        int c = alphDigitStr.length();

        double np = ((double) a / c) * 100;
        double cp = ((double) b / c) * 100;

        feature_list += a + "," + b + "," + c + "," + String.format("%.02f", np) + "," + String.format("%.02f", cp) + ",?";


        return heading+feature_list;

    }

    int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    public boolean classify(String text) {
        try{
        // convert String into InputStream
        String result = createARFF(text);
        InputStream is = new ByteArrayInputStream(result.getBytes());

        // read it with BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        // BufferedReader reader = new BufferedReader
        Instances unlabeled = new Instances(reader);
        reader.close();
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        // create copy
        Instances labeled = new Instances(unlabeled);

        RandomForest tclassifier = (RandomForest) SerializationHelper.read(new FileInputStream("./src/main/resources/CC.model"));
        double pred = tclassifier.classifyInstance(unlabeled.instance(0));
        System.out.println("Result:"+pred);

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

