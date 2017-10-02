/*
 * Copyright 2017 SWIS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package core.classifier;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is custom class written for classify the incidents related to the Credit Cards.
 *
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "Credit Card", filePath = "./leakhawk-core/src/main/resources/CC.model")
//@ContentPattern(patternName = "Credit Card", filePath = "CC.model")
public class CCClassifier extends ContentClassifier {

    private Pattern ccCardPattern;
    private Pattern patterncc1;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private ArrayList<Pattern> trigramPatternList;
    private Pattern relatedTerms1Pattern;
    private Pattern relatedTerms2Pattern;
    private Pattern relatedTerms3Pattern;
    private Pattern relatedTerms4Pattern;
    private Pattern relatedTerms5Pattern;
    private Pattern relatedTerms6Pattern;
    private Pattern digitPattern;
    private Pattern alphaPattern;
    private Pattern alphDigitPattern;
    private RandomForest tclassifier;
    private String headingCC = "@relation CC\n" +
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

    public CCClassifier(String model,String name) {
        super(model, name);
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(model);
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

    @Override
    public int getSensivityLevel(String post){
        int creditCardNumberCount = extractCCNumberCount(post);
        if ((creditCardNumberCount < 5 && creditCardNumberCount > 0)) {
            return 1;
        } else if ((creditCardNumberCount < 20) && (creditCardNumberCount > 5)) {
            return 2;
        } else if (creditCardNumberCount > 20) {
            return 3;
        }
        return 0;
    }


    public int extractCCNumberCount(String post) {
        ccCardPattern = Pattern.compile("[2-6][0-9]{3}([ -]?)[0-9]{4}([ -]?)[0-9]{4}([ -]?)[0-9]{3,4}([ -]?)[0-9]{0,3}[?^a-zA-Z]?");
        Matcher matcherCC = ccCardPattern.matcher(post);
        int CC_Count = getMatchingCount(matcherCC);
        return CC_Count;
    }


}
