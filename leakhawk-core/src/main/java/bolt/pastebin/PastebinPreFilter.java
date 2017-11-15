/*
 *    Copyright 2017 SWIS
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

package bolt.pastebin;

import bolt.core.LeakHawkFilter;
import exception.LeakHawkClassifierLoadingException;
import model.Post;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.omg.CORBA.SystemException;
import util.LeakHawkConstant;
import weka.classifiers.misc.SerializedClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Stopwords;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Udeshika Sewwandi
 * @author Warunika Amali
 */
public class PastebinPreFilter extends LeakHawkFilter {

    private ArrayList<String> codeWordsList;
    private ArrayList<String> gameWordsList;
    private ArrayList<String> sportsWordsList;
    private ArrayList<String> pornWordsList;
    private ArrayList<String> greetingsWordsList;
    private ArrayList<Pattern> codeWordsPatternList;
    private ArrayList<Pattern> gameWordsPatternList;
    private ArrayList<Pattern> sportsWordsPatternList;
    private ArrayList<Pattern> pornWordsPatternList;
    private ArrayList<Pattern> greetingsWordsPatternList;
    private SerializedClassifier tclassifier;
    private String headingPreFilter = "@relation PF\n" +
            "\n" +
            "@attribute $PF1 numeric\n" +
            "@attribute $PF2 numeric\n" +
            "@attribute $PF3 numeric\n" +
            "@attribute $PF4 numeric\n" +
            "@attribute $PF5 numeric\n" +
            "@attribute $PF6 numeric\n" +
            "@attribute $PF7 numeric\n" +
            "@attribute $PF8 numeric\n" +
            "@attribute $PF9 numeric\n" +
            "@attribute $PF10 numeric\n" +
            "@attribute $PF11 numeric\n" +
            "@attribute $PF12 numeric\n" +
            "@attribute $PF13 numeric\n" +
            "@attribute $PF14 numeric\n" +
            "@attribute $PF15 numeric\n" +
            "@attribute $PF16 numeric\n" +
            "@attribute $PF17 numeric\n" +
            "@attribute $PF18 numeric\n" +
            "@attribute $PF19 numeric\n" +
            "@attribute $PF20 numeric\n" +
            "@attribute $PF21 numeric\n" +
            "@attribute $PF22 numeric\n" +
            "@attribute $PF23 numeric\n" +
            "@attribute $PF24 numeric\n" +
            "@attribute $PF25 numeric\n" +
            "@attribute $PF26 numeric\n" +
            "@attribute $PF27 numeric\n" +
            "@attribute $PF28 numeric\n" +
            "@attribute $PF29 numeric\n" +
            "@attribute $PF30 numeric\n" +
            "@attribute $PF31 numeric\n" +
            "@attribute $PF32 numeric\n" +
            "@attribute $PF33 numeric\n" +
            "@attribute $PF34 numeric\n" +
            "@attribute $PF35 numeric\n" +
            "@attribute $PF36 numeric\n" +
            "@attribute $PF37 numeric\n" +
            "@attribute $PF38 numeric\n" +
            "@attribute $PF39 numeric\n" +
            "@attribute $PF40 numeric\n" +
            "@attribute $PF41 numeric\n" +
            "@attribute $PF42 numeric\n" +
            "@attribute $PF43 numeric\n" +
            "@attribute $PF44 numeric\n" +
            "@attribute $PF45 numeric\n" +
            "@attribute $PF46 numeric\n" +
            "@attribute $PF47 numeric\n" +
            "@attribute $PF48 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "@data\n";

    @Override
    public void prepareFilter() {

        try {
//            tclassifier = (RandomForest) weka.core.SerializationHelper.read("/home/neo/Desktop/MyFYP/Project/LeakHawk2.0/LeakHawk/leakhawk-core/src/main/resources/PreFilter.model");
            tclassifier = new SerializedClassifier();
            tclassifier.setModelFile(new File(LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH+"/PreFilter_pb.model"));
        } catch (Exception e) {
            throw new LeakHawkClassifierLoadingException("PreFilter.model file loading error.", e);
        }

        codeWordsPatternList = new ArrayList<>();
        gameWordsPatternList = new ArrayList<>();
        sportsWordsPatternList = new ArrayList<>();
        pornWordsPatternList = new ArrayList<>();
        greetingsWordsPatternList = new ArrayList<>();

        codeWordsList = new ArrayList(Arrays.asList("java|php|python ", " boolean | bool | byte | char | float | int | void | object | struct |namespace", " interface |package| class | function| static | var |implements|override", "#define |#include ", "sizeof|insteadof|isset|echo|def|declare|lambda", "foreach|switch|synchronized |null", "<html>|</html>|html|<head>|</head>|head|<title>|</title>|title|<body>|</body>|body|<h1>|</h1>|<h2>|</h2>|<h3>|</h3>|<h4>|</h4>|<h5>|</h5>|<h6>|</h6>|<img>|</img>|<link>|<link>|<br>|<a>|</a>|<p>|</p>|<style>|</style>", "<script>|</script>", "<div>|</div>"));
        gameWordsList = new ArrayList(Arrays.asList("game|games", "wolfenstein|the new colossus|assassin's creed|middle-earth|shadow of war|destiny|call of duty|dishonored|death of the outsider|dusk|lawbreakers|vanquish|playerUnknown's battlegrounds|friday the 13th|the signal from tolva|ghost recon|wildlands|prey|resident evil|biohazard|bulletstorm|sniper elite |strafe|desync|rising storm|sea of thieves|metal gear survive|world at war|black ops|ghosts|warfare|xbox one"));
        sportsWordsList = new ArrayList(Arrays.asList("arena|play ground|athlete|athletics", "ball|bat|racket|badminton|baseball|basketball|boxing|cricket|paintball|rugby|table tennis|tennis|taekwondo|volley ball|surfing|rafting|relay|marathon|field|fielder|fielding", "bronze medal|gold medal|silver medal|competitor|winner|winning|Gym|gymnast|gymnastics|gymnasium", "goal|goalie|race|racer|racing|Ride|riding|run|runner|running|swim|swimmer|swimming|Weightlifter|weightlifting|weights", "team|teammate|umpire|Olympics|IPL|World Cup|World Series", "Wrestler|wrestling", "Sports"));
        pornWordsList = new ArrayList(Arrays.asList("loved|lover|love|loves|cuddle", "kiss|kisses|Hug|hugs", "homo|homo sexual|gay|lesbian|virgin", "sex|seduce|intercouse|fingering|lust|makeout|foreplay|rape,rapist", "erection|erectile|erect|erotic", "pubic|dick|penis|cocks|cock|pussy|vagina|womb|ass|anus|anal|butt|butts", "prostitute|prostate|slut", "genital|pregnant|abortion", "condom|condoms", "breast|breasts|nipple|boob|boobs|tit|tits|aroused|horny|orgasm", "porn|pornography|naked|nude"));
        greetingsWordsList = new ArrayList(Arrays.asList("blessings|greetings|gratitude|best wishes", "celebrate|celebration", " joy|pleasure |laughte r", "health |prosperity |success|fortune ", "season|new year|coming year", "chritmas|eid|eid mubarak"));

        for (String word : codeWordsList) {
            codeWordsPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        for (String word : gameWordsList) {
            gameWordsPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        for (String word : sportsWordsList) {
            sportsWordsPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        for (String word : pornWordsList) {
            pornWordsPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        for (String word : greetingsWordsList) {
            greetingsWordsPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

    }

    @Override
    protected String getBoltName() {
        return LeakHawkConstant.PASTEBIN_PRE_FILTER;
    }

    @Override
    public boolean isFilterPassed(Post post) {
        // Convert the pastebin post to the lower case
        post.setPostText(post.getPostText().toLowerCase());

        // Return true if post needs to be forwarded to the next bolt
        // Return false if post needs not to be forwarded to the next bolt
        if (isPassedPreFilter(post.getTitle(), post.getPostText(), post)) {
            post.setNextOutputStream(LeakHawkConstant.P_PRE_FILTER_TO_CONTEXT_FILTER);
            increaseOutCount();
            return true;
        }
        return false;
    }

    /**
     * This method will check whether the PreFilter is passed
     *
     * @param title
     * @param post
     * @return
     */
    private boolean isPassedPreFilter(String title, String post,Post originalPost) {

        title = title.toLowerCase();
        post = post.toLowerCase();

        boolean isPrefilterPassed = false;

        //check whether the post is empty
        if (post.isEmpty()) {
            isPrefilterPassed = false;
        }//check whether the post is a test post
        else if (post.contains("test") || title.contains("test")) {
            isPrefilterPassed = false;
        }
        //check whether the post is in English
        else if(isPostEnglish(post)){
            /*remove stop words only in a English language post*/
            String preprocessedText = stopWordsRemoval(post);
            /*set the post text with the stop words removed text*/
            originalPost.setPostText(preprocessedText);
            isPrefilterPassed = isNotFilteredOut(preprocessedText, title);
            //isPrefilterPassed = isNotFilteredOut(post, title);
        }

        return isPrefilterPassed;
    }

    /**
     * Returns the post by removing stop words related to English language
     * @param post
     * @return stop words removed post
     */
    public String stopWordsRemoval(String post){
        Stopwords stpWord = new Stopwords();

        /*remove words from stop word list which may appear in program codes*/
        stpWord.remove("and");
        stpWord.remove("as");
        stpWord.remove("do");
        stpWord.remove("for");
        stpWord.remove("i");
        stpWord.remove("if");
        stpWord.remove("is");
        stpWord.remove("in");
        stpWord.remove("not");
        stpWord.remove("of");
        stpWord.remove("on");
        stpWord.remove("or");
        stpWord.remove("this");

        StringBuilder stringBuilder = new StringBuilder(post);
        /*split string from spaces*/
        String[] words = stringBuilder.toString().split("\\s");
        String fullString = "";

        /*replace stop words with a space*/
        for(int i=0;i<words.length;i++){
            if(stpWord.is(words[i])){
                words[i] = "";
            }
        }

        /*concatenate string without stop words*/
        for(String word:words){
            fullString+=word+" ";
        }
        return fullString;
    }

    /**
     * This method will classify whether the post is English
     *
     * @param text
     * @return
     */

    private boolean isPostEnglish(String text) throws SystemException {

        LanguageDetector detector = null;
        try {
            detector = new OptimaizeLangDetector().loadModels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LanguageResult result = detector.detect(text);
        if("en".equals(result.getLanguage()) | "so".equals(result.getLanguage())){
            return true;
        }

        return false;
    }

    /**
     * This method will classify whether the post is filtered out or in
     *
     * @param text
     * @param title
     * @return
     */
    private boolean isNotFilteredOut(String text, String title) {
        try {
            // convert String into InputStream
            String result = createARFF(text, title);
            InputStream is = new ByteArrayInputStream(result.getBytes());

            // wrap it with buffered reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            //convert into a set of instances
            Instances unlabeled = new Instances(reader);
            reader.close();
            //set the class index to last value of the instance
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // create copy
            Instances labeled = new Instances(unlabeled);

            //set options for the classifier
            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            //predict class for the unseen text
            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
            labeled.instance(0).setClassValue(pred);

            //get the predicted class value
            String classLabel = unlabeled.classAttribute().value((int) pred);

            //if class is pos there's an evidence found
            if ("pos".equals(classLabel)) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create arff file for the predicting text and the title
     * @param text
     * @param title
     * @return
     */
    private String createARFF(String text, String title) {
        String feature_list = "";

        //check the pattern match for text and title for all the cases
        for (Pattern pattern : codeWordsPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : gameWordsPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : gameWordsPatternList) {
            Matcher matcher = pattern.matcher(title);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : sportsWordsPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : pornWordsPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : pornWordsPatternList) {
            Matcher matcher = pattern.matcher(title);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : greetingsWordsPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        //add unknown class for the feature vector
        feature_list += "?";
        return headingPreFilter + feature_list;
    }

    public int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkConstant.P_PRE_FILTER_TO_CONTEXT_FILTER);

        return outputStream;
    }
}
