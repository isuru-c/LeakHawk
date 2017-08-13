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
public class ECClassifier extends ContentClassifier {
    Pattern titlePattern;
    ArrayList<Pattern> unigramPatternList;

    Pattern relatedPattern1;
    Pattern relatedPattern2;
    Pattern relatedPattern3;
    Pattern relatedPattern4;
    Pattern relatedPattern5;
    Pattern relatedPattern6;
    Pattern relatedPattern7;

    public ECClassifier() {
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("reply|forward");
        unigramList.add("subject");
        unigramList.add("mailed by");
        unigramList.add("from:|wrote:|to:|subject:");
        unigramList.add("regards");


        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
            unigramPatternList.add(Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        titlePattern = Pattern.compile("conversation|email");
        relatedPattern1 = Pattern.compile( ">" );


    }

    @Override
    public String createARFF(String text, String title) {
        String feature_list = "";

        for (Pattern pattern : unigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }


        Matcher matcherEC = titlePattern.matcher(title);
        feature_list += getMatchingCount(matcherEC) + ",";

        matcherEC = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcherEC) + ",";

        feature_list += ",?";
        return headingEC + feature_list;
    }

    @Override
    public boolean classify(String text, String title) {
        try {
            // convert String into InputStream
            String result = createARFF(text, title);
            InputStream is = new ByteArrayInputStream(result.getBytes());

            // read it with BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // BufferedReader reader = new BufferedReader
            Instances unlabeled = new Instances(reader);
            reader.close();
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // create copy
            Instances labeled = new Instances(unlabeled);

            RandomForest tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/EC.model");
            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
//        System.out.println("Result:"+pred);

            if (pred >= 0.5) {
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

