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
import model.ContentData;
import model.ContentModel;
import model.Post;
import org.apache.storm.tuple.Values;
import util.LeakHawkParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to classify tweets into different sensitive classes
 *
 * @author Isuru Chandima
 */
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
        return LeakHawkParameters.TWEETS_CONTENT_CLASSIFIER;
    }

    @Override
    public void classifyPost(Post post) {

        ContentModel contentModel = new ContentModel();
        post.setContentModel(contentModel);

        List<ContentData> contentDataList = new ArrayList();


        String text = post.getPostText();
        for (Pattern pattern : ccPattern) {
            Matcher matcher = pattern.matcher(text);
            if(getMatchingCount(matcher)!=0){
                ContentData contentData = new ContentData("Credit Card");
                contentDataList.add(contentData);
                contentModel.setContentFound(true);
            }
        }

        for (Pattern pattern : dbPattern) {
            Matcher matcher = pattern.matcher(text);
            if(getMatchingCount(matcher)!=0){
                ContentData contentData = new ContentData("Database");
                contentDataList.add(contentData);
                contentModel.setContentFound(true);
                break;
            }
        }

        for (Pattern pattern : pkPattern) {
            Matcher matcher = pattern.matcher(text);
            if(getMatchingCount(matcher)!=0){
                ContentData contentData = new ContentData("Private Key");
                contentDataList.add(contentData);
                contentModel.setContentFound(true);
                break;
            }
        }

        for (Pattern pattern : ucPattern) {
            Matcher matcher = pattern.matcher(text);
            if(getMatchingCount(matcher)!=0){
                ContentData contentData = new ContentData("User Credentials");
                contentDataList.add(contentData);
                contentModel.setContentFound(true);
                break;
            }
        }

        for (Pattern pattern : eoPattern) {
            Matcher matcher = pattern.matcher(text);
            if(getMatchingCount(matcher)!=0){
                ContentData contentData = new ContentData("Email Only");
                contentDataList.add(contentData);
                contentModel.setContentFound(true);
                break;
            }
        }

        for (Pattern pattern : wdPattern) {
            Matcher matcher = pattern.matcher(text);
            if(getMatchingCount(matcher)!=0){
                ContentData contentData = new ContentData("Website Defacement");
                contentDataList.add(contentData);
                contentModel.setContentFound(true);
                break;
            }
        }

        for (Pattern pattern : daPattern) {
            Matcher matcher = pattern.matcher(text);
            if(getMatchingCount(matcher)!=0){
                ContentData contentData = new ContentData("DNS Attack");
                contentDataList.add(contentData);
                contentModel.setContentFound(true);
                break;
            }
        }

        contentModel.setContentDataList(contentDataList);

        post.setNextOutputStream(LeakHawkParameters.T_CONTENT_CLASSIFIER_TO_SYNTHESIZER);
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

        outputStream.add(LeakHawkParameters.T_CONTENT_CLASSIFIER_TO_SYNTHESIZER);

        return outputStream;
    }

}
