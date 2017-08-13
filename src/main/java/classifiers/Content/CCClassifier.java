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
public class CCClassifier extends ContentClassifier {
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
    RandomForest tclassifier;

    public static void main(String[] args) {
        CCClassifier ccClassifier = new CCClassifier();
        System.out.println("Result is :" + ccClassifier.classify("", "1.txt"));
    }

    public CCClassifier() {
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/CC.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
            if (word.equals("CC")) {
                unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b"));
            } else {
                unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
            }
        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            trigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
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

    @Override
    public String createARFF(String text, String title) {
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
        return headingCC + feature_list;
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


            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
            labeled.instance(0).setClassValue(pred);

            String classLabel = unlabeled.classAttribute().value((int) pred);

            if("CC".equals(classLabel)){
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*@Override
    public boolean classify(String text, String title,String key) {
        try {
            String result = createARFF(text, title);

            BufferedWriter bw = null;
            FileWriter fw = null;
            try {
                fw = new FileWriter("./src/main/java/classifiers/Content/arff/cc" + key + ".arff");
                bw = new BufferedWriter(fw);
                bw.write(result);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null)
                        bw.close();
                    if (fw != null)
                        fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            ProcessBuilder pbVal = new ProcessBuilder("/bin/bash", "/home/neo/Desktop/FinalYearProject/LeakHawk/src/main/java/classifiers/Content/validator/CC_validator.sh", "./src/main/java/classifiers/Content/arff/cc" + key + ".arff");
            final Process processVal = pbVal.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(processVal.getInputStream()));
            String line = br.readLine();
            if(line!=null) {
                if (line.contains("non")) {
                    return false;
                } else if (line.contains("CC")) {
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            File file = new File("./src/main/java/classifiers/Content/arff/cc" + key + ".arff");
            file.delete();
        }
        return false;
    }*/


}

