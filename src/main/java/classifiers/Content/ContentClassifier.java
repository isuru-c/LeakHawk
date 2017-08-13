package classifiers.Content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
public class ContentClassifier {
    String headingCC = "@relation CC\n" +
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

    String headingCF = "@relation CF\n" +
            "\n" +
            "@attribute $CF1 numeric\n" +
            "@attribute $CF2 numeric\n" +
            "@attribute $CF3 numeric\n" +
            "@attribute $CF4 numeric\n" +
            "@attribute $CF5 numeric\n" +
            "@attribute $CF6 numeric\n" +
            "@attribute $CF7 numeric\n" +
            "@attribute $CF8 numeric\n" +
            "@attribute $CF9 numeric\n" +
            "@attribute $CF10 numeric\n" +
            "@attribute $CF11 numeric\n" +
            "@attribute $CF12 numeric\n" +
            "@attribute $CF13 numeric\n" +
            "@attribute $CF14 numeric\n" +
            "@attribute $CF15 numeric\n" +
            "@attribute $CF16 numeric\n" +
            "@attribute $CF17 numeric\n" +
            "@attribute $CF18 numeric\n" +
            "@attribute $CF19 numeric\n" +
            "@attribute $CF20 numeric\n" +
            "@attribute $CF21 numeric\n" +
            "@attribute $CF22 numeric\n" +
            "@attribute $CF23 numeric\n" +
            "@attribute $CF24 numeric\n" +
            "@attribute @@class@@ {CF,non}\n" +
            "\n" +
            "@data\n";

    String headingDA = "@relation train\n" +
            "\n" +
            "@attribute $DA1 numeric\n" +
            "@attribute $DA2 numeric\n" +
            "@attribute $DA3 numeric\n" +
            "@attribute $DA4 numeric\n" +
            "@attribute $DA5 numeric\n" +
            "@attribute $DA6 numeric\n" +
            "@attribute $DA7 numeric\n" +
            "@attribute $DA8 numeric\n" +
            "@attribute $DA9 numeric\n" +
            "@attribute $DA10 numeric\n" +
            "@attribute $DA11 numeric\n" +
            "@attribute $DA12 numeric\n" +
            "@attribute $DA13 numeric\n" +
            "@attribute $DA14 numeric\n" +
            "@attribute $DA15 numeric\n" +
            "@attribute $DA16 numeric\n" +
            "@attribute $DA17 numeric\n" +
            "@attribute $DA18 numeric\n" +
            "@attribute $DA19 numeric\n" +
            "@attribute @@class@@ {DA,non}\n" +
            "\n" +
            "@data\n";

    String headingDB = "@relation DB\n" +
            "\n" +
            "@attribute $DB1 numeric\n" +
            "@attribute $DB2 numeric\n" +
            "@attribute $DB3 numeric\n" +
            "@attribute $DB4 numeric\n" +
            "@attribute $DB5 numeric\n" +
            "@attribute $DB6 numeric\n" +
            "@attribute $DB7 numeric\n" +
            "@attribute $DB8 numeric\n" +
            "@attribute $DB9 numeric\n" +
            "@attribute $DB10 numeric\n" +
            "@attribute $DB11 numeric\n" +
            "@attribute $DB12 numeric\n" +
            "@attribute $DB13 numeric\n" +
            "@attribute $DB14 numeric\n" +
            "@attribute $DB15 numeric\n" +
            "@attribute $DB16 numeric\n" +
            "@attribute $DB17 numeric\n" +
            "@attribute $DB18 numeric\n" +
            "@attribute $DB19 numeric\n" +
            "@attribute $DB20 numeric\n" +
            "@attribute $DB21 numeric\n" +
            "@attribute $DB22 numeric\n" +
            "@attribute $DB23 numeric\n" +
            "@attribute $DB24 numeric\n" +
            "@attribute $DB25 numeric\n" +
            "@attribute @@class@@ {DB,non}\n" +
            "\n" +
            "@data\n";

    String headingEC = "@relation train\n" +
            "\n" +
            "@attribute $EC1 numeric\n" +
            "@attribute $EC2 numeric\n" +
            "@attribute $EC3 numeric\n" +
            "@attribute $EC4 numeric\n" +
            "@attribute $EC5 numeric\n" +
            "@attribute $EC6 numeric\n" +
            "@attribute $EC7 numeric\n" +
            "@attribute @@class@@ {EC,non}\n" +
            "\n" +
            "@data\n";

    String headingEO = "@relation train\n" +
            "\n" +
            "@attribute $EO1 numeric\n" +
            "@attribute $EO2 numeric\n" +
            "@attribute $EO3 numeric\n" +
            "@attribute $EO4 numeric\n" +
            "@attribute $EO5 numeric\n" +
            "@attribute @@class@@ {EO,non}\n" +
            "\n" +
            "@data\n";

    String headingPK = "@relation PK\n" +
            "\n" +
            "@attribute $PK1 numeric\n" +
            "@attribute $PK2 numeric\n" +
            "@attribute $PK3 numeric\n" +
            "@attribute $PK4 numeric\n" +
            "@attribute $PK5 numeric\n" +
            "@attribute $PK6 numeric\n" +
            "@attribute $PK7 numeric\n" +
            "@attribute $PK8 numeric\n" +
            "@attribute $PK9 numeric\n" +
            "@attribute $PK10 numeric\n" +
            "@attribute $PK11 numeric\n" +
            "@attribute $PK12 numeric\n" +
            "@attribute $PK13 numeric\n" +
            "@attribute $PK14 numeric\n" +
            "@attribute $PK15 numeric\n" +
            "@attribute $PK16 numeric\n" +
            "@attribute $PK17 numeric\n" +
            "@attribute $PK18 numeric\n" +
            "@attribute $PK19 numeric\n" +
            "@attribute $PK20 numeric\n" +
            "@attribute $PK21 numeric\n" +
            "@attribute @@class@@ {PK,non}\n" +
            "\n" +
            "@data\n";

    public String createARFF(String text,String title) {
        return null;
    }

   /* public boolean classify(String text,String title,String key) {
        return false;
    }*/

    public boolean classify(String text,String title) {
        return false;
    }

    int getMatchingCount(Matcher matcher) {
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }

    Pattern getCorrectPatten(String word,int type){
        Pattern compile = Pattern.compile(word.replaceAll("\\|","\b|\b"),type);
        return compile;
    }

    Pattern getCorrectPatten(String word){
        Pattern compile = Pattern.compile(word.replaceAll("\\|","\b|\b"));
        return compile;
    }
}
