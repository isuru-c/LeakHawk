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
public class PKClassifier extends ContentClassifier {

    ArrayList<Pattern> unigramPatternList;
    ArrayList<Pattern> bigramPatternList;
    ArrayList<Pattern> trigramPatternList;
    ArrayList<Pattern> fourgramPatternList;

    Pattern relatedPattern1;
    Pattern relatedPattern2;


    public PKClassifier() {
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("PRIVATE");
        unigramList.add("KEY");
        unigramList.add("RSA");
        unigramList.add("SSHRSA");
        unigramList.add("KEY-----");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("PRIVATE KEY");
        bigramList.add("RSA PRIVATE");
        bigramList.add("BEGIN CERTIFICATE");
        bigramList.add("DSA PRIVATE");
        bigramList.add("ENCRYPTED PRIVATE");
        bigramList.add("BEGIN RSA");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("RSA PRIVATE KEY");
        trigramList.add("DSA PRIVATE KEY");
        trigramList.add("BEGIN RSA PRIVATE");
        trigramList.add("END PRIVATE KEY");
        trigramList.add("BEGIN PRIVATE KEY");

        ArrayList<String> fourgramList = new ArrayList<String>();
        trigramList.add("BEGIN RSA PRIVATE KEY");
        trigramList.add("BEGIN DSA PRIVATE KEY");
        trigramList.add("END RSA PRIVATE KEY");


        /*
        *

	PK20=$(grep -oE "[-]{5}[A-Za-z0-9 ]+[-]{5}" "$i"| wc -l);

	#PK related terms
	PK21=$(grep -owiE "PRIVATE KEY|RSA PRIVATE|DSA PRIVATE|ENCRYPTED PRIVATE|BEGIN RSA|RSA PRIVATE KEY|DSA PRIVATE KEY|BEGIN RSA PRIVATE|END PRIVATE KEY|BEGIN PRIVATE KEY" "$i"| wc -l);

        * */



        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
                unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            bigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            trigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        fourgramPatternList = new ArrayList<Pattern>();
        for (String word : fourgramList) {
            fourgramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
        }

        relatedPattern1 = Pattern.compile("[-]{5}[A-Za-z0-9 ]+[-]{5}");
        relatedPattern2 = getCorrectPatten("\\b"+"PRIVATE KEY|RSA PRIVATE|DSA PRIVATE|ENCRYPTED PRIVATE|BEGIN RSA|RSA PRIVATE KEY|DSA PRIVATE KEY|BEGIN RSA PRIVATE|END PRIVATE KEY|BEGIN PRIVATE KEY"+"\\b", Pattern.CASE_INSENSITIVE);
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

        for (Pattern pattern : fourgramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        Matcher matcher = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern2.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        feature_list += "?";
        return headingPK + feature_list;
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

            RandomForest tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/PK.model");
            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
            String classLabel = unlabeled.classAttribute().value((int) pred);

            if("PK".equals(classLabel)){
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

   /* @Override
    public boolean classify(String text, String title,String key) {
        try {
            String result = createARFF(text, title);

            BufferedWriter bw = null;
            FileWriter fw = null;
            try {
                fw = new FileWriter("./src/main/java/classifiers/Content/arff/pk" + key + ".arff");
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

            ProcessBuilder pbVal = new ProcessBuilder("/bin/bash", "/home/neo/Desktop/FinalYearProject/LeakHawk/src/main/java/classifiers/Content/validator/PK_validator.sh", "./src/main/java/classifiers/Content/arff/pk" + key + ".arff");
            final Process processVal = pbVal.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(processVal.getInputStream()));
            String line = br.readLine();
            if(line!=null) {
                if (line.contains("non")) {
                    return false;
                } else if (line.contains("PK")) {
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            File file = new File("./src/main/java/classifiers/Content/arff/pk" + key + ".arff");
            file.delete();
        }
        return false;
    }*/

}
