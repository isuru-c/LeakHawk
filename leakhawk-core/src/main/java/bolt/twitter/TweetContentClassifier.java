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

package bolt.twitter;

import bolt.core.LeakHawkClassifier;
import classifier.Content.Hash_id;
import model.ContentData;
import model.ContentModel;
import model.Post;
import util.LeakHawkConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to classify tweets into different sensitive classes
 *
 * @author Isuru Chandima
 * @author Warunika Amali
 */
@SuppressWarnings("ALL")
public class TweetContentClassifier extends LeakHawkClassifier {

    private ArrayList<String> creditCard;
    private ArrayList<String> database;
    private ArrayList<String> privateKey;
    private ArrayList<String> userCredentials;
    private ArrayList<String> emailOnly;
    private ArrayList<String> websiteDefacement;
    private ArrayList<String> dnsAttack;
    private ArrayList<Pattern> ccPattern;
    private ArrayList<Pattern> dbPattern;
    private ArrayList<Pattern> pkPattern;
    private ArrayList<Pattern> ucPattern;
    private ArrayList<Pattern> eoPattern;
    private ArrayList<Pattern> wdPattern;
    private ArrayList<Pattern> daPattern;

    public TweetContentClassifier(){
        creditCard =new ArrayList<>(Arrays.asList("cvv|cvc2|cav2","card","number","credit","cc","card number","credit card","account number","card type","card information","card hack","atm pin","account information","credit card number","credit card information","card verification code","card verification number","bank account number","visa card number","credit_card","card_dump","working_card","cc_dump","skimmed","card_hack"));
        database =new ArrayList<>(Arrays.asList("database","table","schema|db","postgresql|mysql|mssql|oracle db|db2","dbms|data base:|db detection:", "database dump|db user :|db Version :","hacked database","leaked database|database leaked","db leak","dumped from|dumped by","database dump|db dump|db leak|data base dump|data base leak|database hack|db hack|login dump","available databases"));
        privateKey =new ArrayList<>(Arrays.asList("public_key|private_key"," rsa ","sshrsa","private key","rsa private","dsa private","encrypted private","rsa private key","dsa private key"));
        userCredentials =new ArrayList<>(Arrays.asList("password|passw0rd|passwd|passwort","user|username|admin","user name","password dump","Dumped from|dumped by","login dump","credential dump"));
        emailOnly =new ArrayList<>(Arrays.asList("email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack","emails leaked|domains hacked|leaked email list|email list leaked|leaked emails|email_hacked|emails_hacked|email|emails_leak|email_dump|emails_dump|email_dumps|email-list|leaked_email|email_hack"));
        websiteDefacement =new ArrayList<>(Arrays.asList("defaced|defacement","domain hacked|website[s] hacked","defaced by","site hacked|got hacked","sites defaced|mass deface","sites got Hacked","mirror-zone.org|dark-h.net|zone-h.org|zone-hc.com|dark-h.org|turk-h.org|Zone-hc.com|add-attack.com/mirror|zone-db.com|hack-db.com"));
        dnsAttack =new ArrayList<>(Arrays.asList("dns-brute|dnsrecon|fierce|tsunami|dnsdict6|axfr","dns leaked|dns fuck3d|zone transfer|dns|enumeration_attack","dns_enumeration", "dns enumeration attack|dns enumeration|misconfigured dns|dns cache snooping"));

    }

    @Override
    public void prepareClassifier() {
        ccPattern = new ArrayList<>();
        for (String word : creditCard) {
            ccPattern.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        dbPattern = new ArrayList<>();
        for (String word : database) {
            dbPattern.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        pkPattern = new ArrayList<>();
        for (String word : privateKey) {
            pkPattern.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        ucPattern = new ArrayList<>();
        for (String word : userCredentials) {
            ucPattern.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        eoPattern = new ArrayList<>();
        for (String word : emailOnly) {
            eoPattern.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        wdPattern = new ArrayList<>();
        for (String word : websiteDefacement) {
            wdPattern.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        daPattern = new ArrayList<>();
        for (String word : dnsAttack) {
            daPattern.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

    }

    @Override
    protected String getBoltName() {
        return LeakHawkConstant.TWEETS_CONTENT_CLASSIFIER;
    }

    @Override
    public void classifyPost(Post post) {

        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);

        List<ContentData> contentDataList = new ArrayList();

        String text = post.getPostText();
        for (Pattern pattern : ccPattern) {
            boolean matched = matchWithPattern(pattern, text, contentDataList, contentModel, "Credit Card", getCreditCardSensivityLevel(text));
            if(matched==false && post.getUrlList().size()>0) {
                boolean urlMatched = matchWithPattern(pattern, post.getUrlContent(), contentDataList, contentModel, "Credit Card", getCreditCardSensivityLevel(post.getUrlContent()));
                if(urlMatched){
                    matched = true;
                }
            }
            if(matched){
                break;
            }
        }

        for (Pattern pattern : dbPattern) {
            boolean matched = matchWithPattern(pattern, text, contentDataList, contentModel, "Database", getDBSensivityLevel(text));
            if(matched==false && post.getUrlList().size()>0) {
                boolean urlMatched = matchWithPattern(pattern, post.getUrlContent(), contentDataList, contentModel, "Database", getDBSensivityLevel(post.getUrlContent()));
                if(urlMatched){
                    matched = true;
                }
            }
            if(matched){
                break;
            }
        }

        for (Pattern pattern : pkPattern) {
            boolean matched = matchWithPattern(pattern, text, contentDataList, contentModel, "Private Key", getPKSensivityLevel(text));
            if(matched==false && post.getUrlList().size()>0) {
                boolean urlMatched = matchWithPattern(pattern, post.getUrlContent(), contentDataList, contentModel, "Private Key", getPKSensivityLevel(post.getUrlContent()));
                if(urlMatched){
                    matched = true;
                }
            }
            if(matched){
                break;
            }
        }

        for (Pattern pattern : ucPattern) {
            boolean matched = matchWithPattern(pattern, text, contentDataList, contentModel, "User Credentials", getUCSensivityLevel(text));
            if(matched==false && post.getUrlList().size()>0) {
                boolean urlMatched = matchWithPattern(pattern, post.getUrlContent(), contentDataList, contentModel, "User Credentials", getUCSensivityLevel(post.getUrlContent()));
                if(urlMatched){
                    matched = true;
                }
            }
            if(matched){
                break;
            }
        }

        for (Pattern pattern : eoPattern) {
            boolean matched = matchWithPattern(pattern, text, contentDataList, contentModel, "Email Only", getEOSensivityLevel(text));
            if(matched==false && post.getUrlList().size()>0) {
                boolean urlMatched = matchWithPattern(pattern, post.getUrlContent(), contentDataList, contentModel, "Email Only", getEOSensivityLevel(post.getUrlContent()));
                if(urlMatched){
                    matched = true;
                }
            }
            if(matched){
                break;
            }
        }

        for (Pattern pattern : wdPattern) {
            boolean matched = matchWithPattern(pattern, text, contentDataList, contentModel, "Website Defacement", getWDSensivityLevel(text));
            if(matched==false && post.getUrlList().size()>0) {
                boolean urlMatched = matchWithPattern(pattern, post.getUrlContent(), contentDataList, contentModel, "Website Defacement", getWDSensivityLevel(post.getUrlContent()));
                if(urlMatched){
                    matched = true;
                }
            }
            if(matched){
                break;
            }
        }

        for (Pattern pattern : daPattern) {
            boolean matched = matchWithPattern(pattern, text, contentDataList, contentModel, "DNS Attack", getDASensivityLevel(text));
            if(matched==false && post.getUrlList().size()>0) {
                boolean urlMatched = matchWithPattern(pattern, post.getUrlContent(), contentDataList, contentModel, "DNS Attack", getDASensivityLevel(post.getUrlContent()));
                if(urlMatched){
                    matched = true;
                }
            }
            if(matched){
                break;
            }
        }

        contentModel.setContentDataList(contentDataList);

        if(contentModel.isContentFound()){
            increaseOutCount();
        }

        post.setNextOutputStream(LeakHawkConstant.T_CONTENT_CLASSIFIER_TO_SYNTHESIZER);
    }

    boolean matchWithPattern(Pattern pattern,String text,List contentDataList, ContentModel contentModel,String type, int level){
        Matcher matcher = pattern.matcher(text);
        if(getMatchingCount(matcher)!=0){
            ContentData contentData = new ContentData(type,level);
            contentDataList.add(contentData);
            contentModel.setContentFound(true);
            return true;
        }
        return false;
    }

    int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkConstant.T_CONTENT_CLASSIFIER_TO_SYNTHESIZER);

        return outputStream;
    }


    public int getCreditCardSensivityLevel(String post){
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
        Pattern ccCardPattern = Pattern.compile("[2-6][0-9]{3}([ -]?)[0-9]{4}([ -]?)[0-9]{4}([ -]?)[0-9]{3,4}([ -]?)[0-9]{0,3}[?^a-zA-Z]?");
        Matcher matcherCC = ccCardPattern.matcher(post);
        int CC_Count = getMatchingCount(matcherCC);
        return CC_Count;
    }


    public int getCFSensivityLevel(String post) {
        if (post.contains("enable password")) {
            return 3;
        }
        return 1;
    }

    public int getDASensivityLevel(String post){
        ArrayList<String> DAlist = new ArrayList<String>(Arrays.asList("lanka", "lk", "ceylon", "sinhala", "buddhist", "colombo", "kandy", "kurunegala", "gampaha", "mahinda", "sirisena", "ranil"));
        int domainCount = 0;
        int count=0;
        for (String i : DAlist) {
            if (post.contains(i)) {
                count++;
            }
        }
        if (domainCount < 10) {
            return 2;
        } else if (domainCount >= 10) {
            return 3;
        }
        return 1;
    }


    public int getDBSensivityLevel(String post) {
        return 2;
    }

    public int getECSensivityLevel(String post){
        ArrayList<String> ECList = new ArrayList<String>(Arrays.asList("CONFIDENTIAL", "secret", "do not disclose"));
        int ecCount = 0;
        for (String i : ECList) {
            if (post.contains(i)) {
                ecCount++;
            }
        }
        if (ecCount > 0) {
            return 3;
        }
        return 1;
    }

    public int getEOSensivityLevel(String post){
        int email_count = EOCounter(post);
        if (email_count < 50) {
            return 1;
        }
        return 2;
    }

    public int EOCounter(String post) {
        Pattern emailPattern = Pattern.compile("(([a-zA-Z]|[0-9])|([-]|[_]|[.]))+[@](([a-zA-Z0-9])|([-])){2,63}([.]((([a-zA-Z0-9])|([-])){2,63})){1,4}");
        Matcher matcherEO = emailPattern.matcher(post);
        int EO_Count = getMatchingCount(matcherEO);
        return EO_Count;
    }

    public int getPKSensivityLevel(String post){
        return 3;
    }

    public int getUCSensivityLevel(String post) {
        int hashCount = extractHashCount(post);
        if ((hashCount < 5 && hashCount > 0)) {
            return 1;
        } else if ((hashCount < 20) && (hashCount > 5)) {
            return 2;
        } else if (hashCount > 20) {
            return 3;
        }
        return 0;
    }

    /*
    Getting the hash count in the post
     */
    public int extractHashCount(String post) {
        int hashCount = 0;
        Hash_id hash_id = new Hash_id();

        String[] words= post.split(" |\n");
        for(String word:words){
            if(hash_id.isHash(word)==true) hashCount++;
        }
        return hashCount;
    }


    public int getWDSensivityLevel(String post) {
        int urlCount = extractUrlCount(post);

        if ((urlCount < 5 && urlCount > 0)) {
            return 1;
        } else if ((urlCount < 20) && (urlCount > 5)) {
            return 2;
        } else if (urlCount > 20) {
            return 3;
        }
        return 0;
    }

    /*
    Getting the hash count in the post
     */
    public int extractUrlCount(String post) {
        int urlCount=0;
        String[] words= post.split("\\s| ");
        for(String word:words){
            if (word.matches("\\b((https?):|(www))\\S+")) {
                // System.out.println("URL =" + word);
                urlCount++;
            }
        }
        return urlCount;
    }




}
