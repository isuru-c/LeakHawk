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
package classifier.Content;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Warunika Amali
 */
@SuppressWarnings("ALL")
@ContentPattern(patternName = "Website Defacement", filePath = "./src/main/resources/WD.model")
public class WDClassifier extends ContentClassifier{

    private int urlCount;
    private ArrayList<Pattern> unigramPatternList;
    private ArrayList<Pattern> bigramPatternList;
    private ArrayList<Pattern> trigramPatternList;
    private Pattern relatedTerms1Pattern;
    private Pattern relatedTerms2Pattern;
    private Pattern relatedTerms3Pattern;
    private RandomForest tclassifier;
    private String headingWD = "@relation WD\n" +
            "\n" +
            "@attribute $WD1 numeric\n" +
            "@attribute $WD2 numeric\n" +
            "@attribute $WD3 numeric\n" +
            "@attribute $WD4 numeric\n" +
            "@attribute $WD5 numeric\n" +
            "@attribute $WD6 numeric\n" +
            "@attribute $WD7 numeric\n" +
            "@attribute $WD8 numeric\n" +
            "@attribute $WD9 numeric\n" +
            "@attribute $WD10 numeric\n" +
            "@attribute $WD11 numeric\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";

    public WDClassifier(String model, String name) {
        super(model, name);
        try {
            tclassifier = (RandomForest) weka.core.SerializationHelper.read(this.getClass().getClassLoader().getResourceAsStream("WD.model"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("defaced|defacement");
        unigramList.add("mirror");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("domain hacked|website[s] hacked");
        bigramList.add("defaced by|leaked by");
        bigramList.add("site hacked|got hacked");
        bigramList.add("sites defaced|mass deface");

        ArrayList<String> trigramList =new ArrayList<String>();
        trigramList.add("sites got Hacked");

        unigramPatternList = new ArrayList<>();
        for (String word : unigramList) {
            unigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        bigramPatternList = new ArrayList<>();
        for (String word : bigramList) {
            bigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<>();
        for (String word : trigramList) {
            trigramPatternList.add(Pattern.compile(word, Pattern.CASE_INSENSITIVE));
        }

        relatedTerms1Pattern =Pattern.compile("(web)*site(s)*[A-Za-z0-9_ ]*hack(ed)*|deface(d) by|hack(ed) by|deface|pwned|hacked|leaked", Pattern.CASE_INSENSITIVE);
        relatedTerms2Pattern = Pattern.compile("sited hacked by|websited hacked by|website hacked by|site hacked by|websited hacked|domain hack|defaced|leaked by|site deface|mass deface", Pattern.CASE_INSENSITIVE);
        relatedTerms3Pattern = Pattern.compile("mirror-zone.org|dark-h.net|zone-h.org|zone-hc.com|dark-h.org|turk-h.org|Zone-hc.com|add-attack.com/mirror|zone-db.com|hack-db.com", Pattern.CASE_INSENSITIVE);

    }

    /*
    Creating the arff file of new post
     */
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

        Matcher matcher = relatedTerms1Pattern.matcher(title);
        int matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";

        matcher = relatedTerms2Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";

        matcher = relatedTerms3Pattern.matcher(text);
        matchingCount = getMatchingCount(matcher);
        feature_list += matchingCount + ",";

        extractUrlCount(text);
        feature_list +=urlCount + ",";
        feature_list += "?";

        return headingWD + feature_list;
    }


    @Override
    public boolean classify(String text, String title) {
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
            if("pos".equals(classLabel)){
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
    public int getSensivityLevel(String post) {

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
    public void extractUrlCount(String post) {

        urlCount=0;
        String[] words= post.split("\\s| ");
        for(String word:words){
            if (word.matches("\\b((https?):|(www))\\S+")) {
                // System.out.println("URL =" + word);
                urlCount++;
            }
        }
    }
}
