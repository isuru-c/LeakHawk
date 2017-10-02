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

package bolt;

import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import weka.classifiers.misc.SerializedClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author sewwandi
 */
public class PastebinPreFilter extends BaseRichBolt {

    private OutputCollector collector;
    private ArrayList keyWordList;

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
    Pattern relatedPattern1;
    //private Connection connection;
   /* private TextDirectoryLoader loader;
    private Instances dataRaw;
    private StringToWordVector filter;
    private Instances dataFiltered;
    private List<String> stopWordList;
    private String regex;
    private String regex1;*/
    //private RandomForest classifier;
    SerializedClassifier sclassifier;

    String headingPreFilter ="@relation PF\n" +
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
            "@attribute $PF49 numeric\n" +
            "@attribute $PF50 numeric\n" +
            "@attribute $PF51 numeric\n" +
            "@attribute $PF52 numeric\n" +
            "@attribute $PF53 numeric\n" +
            "@attribute $PF54 numeric\n" +
            "@attribute $PF55 numeric\n" +
            "@attribute $PF56 numeric\n" +
            "@attribute $PF57 numeric\n" +
            "@attribute $PF58 numeric\n" +
            "@attribute $PF59 numeric\n" +
            "@attribute $PF60 numeric\n" +
            "@attribute $PF61 numeric\n" +
            "@attribute $PF62 numeric\n" +
            "@attribute $PF63 numeric\n" +
            "@attribute $PF64 numeric\n" +
            "@attribute $PF65 numeric\n" +
            "@attribute $PF66 numeric\n" +
            "@attribute $PF67 numeric\n" +
            "@attribute $PF68 numeric\n" +
            "@attribute $PF69 numeric\n" +
            "@attribute $PF70 numeric\n" +
            "@attribute $PF71 numeric\n" +
            "@attribute $PF72 numeric\n" +
            "@attribute $PF73 numeric\n" +
            "@attribute $PF74 numeric\n" +
            "@attribute $PF75 numeric\n" +
            "@attribute $PF76 numeric\n" +
            "@attribute $PF77 numeric\n" +
            "@attribute $PF78 numeric\n" +
            "@attribute $PF79 numeric\n" +
            "@attribute $PF80 numeric\n" +
            "@attribute $PF81 numeric\n" +
            "@attribute $PF82 numeric\n" +
            "@attribute $PF83 numeric\n" +
            "@attribute $PF84 numeric\n" +
            "@attribute $PF85 numeric\n" +
            "@attribute $PF86 numeric\n" +
            "@attribute $PF87 numeric\n" +
            "@attribute $PF88 numeric\n" +
            "@attribute $PF89 numeric\n" +
            "@attribute $PF90 numeric\n" +
            "@attribute $PF91 numeric\n" +
            "@attribute $PF92 numeric\n" +
            "@attribute $PF93 numeric\n" +
            "@attribute $PF94 numeric\n" +
            "@attribute $PF95 numeric\n" +
            "@attribute $PF96 numeric\n" +
            "@attribute $PF97 numeric\n" +
            "@attribute $PF98 numeric\n" +
            "@attribute $PF99 numeric\n" +
            "@attribute $PF100 numeric\n" +
            "@attribute $PF101 numeric\n" +
            "@attribute $PF102 numeric\n" +
            "@attribute $PF103 numeric\n" +
            "@attribute $PF104 numeric\n" +
            "@attribute $PF105 numeric\n" +
            "@attribute $PF106 numeric\n" +
            "@attribute $PF107 numeric\n" +
            "@attribute $PF108 numeric\n" +
            "@attribute $PF109 numeric\n" +
            "@attribute $PF110 numeric\n" +
            "@attribute $PF111 numeric\n" +
            "@attribute $PF112 numeric\n" +
            "@attribute $PF113 numeric\n" +
            "@attribute $PF114 numeric\n" +
            "@attribute $PF115 numeric\n" +
            "@attribute $PF116 numeric\n" +
            "@attribute $PF117 numeric\n" +
            "@attribute $PF118 numeric\n" +
            "@attribute $PF119 numeric\n" +
            "@attribute $PF120 numeric\n" +
            "@attribute $PF121 numeric\n" +
            "@attribute $PF122 numeric\n" +
            "@attribute $PF123 numeric\n" +
            "@attribute $PF124 numeric\n" +
            "@attribute $PF125 numeric\n" +
            "@attribute $PF126 numeric\n" +
            "@attribute $PF127 numeric\n" +
            "@attribute $PF128 numeric\n" +
            "@attribute $PF129 numeric\n" +
            "@attribute $PF130 numeric\n" +
            "@attribute $PF131 numeric\n" +
            "@attribute $PF132 numeric\n" +
            "@attribute $PF133 numeric\n" +
            "@attribute $PF134 numeric\n" +
            "@attribute $PF135 numeric\n" +
            "@attribute $PF136 numeric\n" +
            "@attribute $PF137 numeric\n" +
            "@attribute $PF138 numeric\n" +
            "@attribute $PF139 numeric\n" +
            "@attribute $PF140 numeric\n" +
            "@attribute $PF141 numeric\n" +
            "@attribute $PF142 numeric\n" +
            "@attribute $PF143 numeric\n" +
            "@attribute $PF144 numeric\n" +
            "@attribute $PF145 numeric\n" +
            "@attribute $PF146 numeric\n" +
            "@attribute $PF147 numeric\n" +
            "@attribute $PF148 numeric\n" +
            "@attribute $PF149 numeric\n" +
            "@attribute $PF150 numeric\n" +
            "@attribute $PF151 numeric\n" +
            "@attribute $PF152 numeric\n" +
            "@attribute $PF153 numeric\n" +
            "@attribute $PF154 numeric\n" +
            "@attribute $PF155 numeric\n" +
            "@attribute $PF156 numeric\n" +
            "@attribute $PF157 numeric\n" +
            "@attribute $PF158 numeric\n" +
            "@attribute $PF159 numeric\n" +
            "@attribute $PF160 numeric\n" +
            "@attribute $PF161 numeric\n" +
            "@attribute $PF162 numeric\n" +
            "@attribute $PF163 numeric\n" +
            "@attribute $PF164 numeric\n" +
            "@attribute $PF165 numeric\n" +
            "@attribute $PF166 numeric\n" +
            "@attribute $PF167 numeric\n" +
            "@attribute $PF168 numeric\n" +
            "@attribute $PF169 numeric\n" +
            "@attribute $PF170 numeric\n" +
            "@attribute $PF171 numeric\n" +
            "@attribute $PF172 numeric\n" +
            "@attribute $PF173 numeric\n" +
            "@attribute $PF174 numeric\n" +
            "@attribute $PF175 numeric\n" +
            "@attribute $PF176 numeric\n" +
            "@attribute $PF177 numeric\n" +
            "@attribute $PF178 numeric\n" +
            "@attribute $PF179 numeric\n" +
            "@attribute $PF180 numeric\n" +
            "@attribute $PF181 numeric\n" +
            "@attribute $PF182 numeric\n" +
            "@attribute $PF183 numeric\n" +
            "@attribute $PF184 numeric\n" +
            "@attribute $PF185 numeric\n" +
            "@attribute $PF186 numeric\n" +
            "@attribute $PF187 numeric\n" +
            "@attribute $PF188 numeric\n" +
            "@attribute $PF189 numeric\n" +
            "@attribute $PF190 numeric\n" +
            "@attribute $PF191 numeric\n" +
            "@attribute $PF192 numeric\n" +
            "@attribute $PF193 numeric\n" +
            "@attribute $PF194 numeric\n" +
            "@attribute $PF195 numeric\n" +
            "@attribute $PF196 numeric\n" +
            "@attribute $PF197 numeric\n" +
            "@attribute $PF198 numeric\n" +
            "@attribute $PF199 numeric\n" +
            "@attribute $PF200 numeric\n" +
            "@attribute $PF201 numeric\n" +
            "@attribute $PF202 numeric\n" +
            "@attribute $PF203 numeric\n" +
            "@attribute $PF204 numeric\n" +
            "@attribute $PF205 numeric\n" +
            "@attribute $PF206 numeric\n" +
            "@attribute $PF207 numeric\n" +
            "@attribute $PF208 numeric\n" +
            "@attribute $PF209 numeric\n" +
            "@attribute $PF210 numeric\n" +
            "@attribute $PF211 numeric\n" +
            "@attribute $PF212 numeric\n" +
            "@attribute $PF213 numeric\n" +
            "@attribute $PF214 numeric\n" +
            "@attribute $PF215 numeric\n" +
            "@attribute $PF216 numeric\n" +
            "@attribute $PF217 numeric\n" +
            "@attribute $PF218 numeric\n" +
            "@attribute $PF219 numeric\n" +
            "@attribute $PF220 numeric\n" +
            "@attribute $PF221 numeric\n" +
            "@attribute $PF222 numeric\n" +
            "@attribute $PF223 numeric\n" +
            "@attribute $PF224 numeric\n" +
            "@attribute $PF225 numeric\n" +
            "@attribute $PF226 numeric\n" +
            "@attribute $PF227 numeric\n" +
            "@attribute $PF228 numeric\n" +
            "@attribute $PF229 numeric\n" +
            "@attribute $PF230 numeric\n" +
            "@attribute $PF231 numeric\n" +
            "@attribute $PF232 numeric\n" +
            "@attribute $PF233 numeric\n" +
            "@attribute $PF234 numeric\n" +
            "@attribute $PF235 numeric\n" +
            "@attribute $PF236 numeric\n" +
            "@attribute $PF237 numeric\n" +
            "@attribute $PF238 numeric\n" +
            "@attribute $PF239 numeric\n" +
            "@attribute $PF240 numeric\n" +
            "@attribute $PF241 numeric\n" +
            "@attribute $PF242 numeric\n" +
            "@attribute $PF243 numeric\n" +
            "@attribute $PF244 numeric\n" +
            "@attribute $PF245 numeric\n" +
            "@attribute $PF246 numeric\n" +
            "@attribute $PF247 numeric\n" +
            "@attribute $PF248 numeric\n" +
            "@attribute $PF249 numeric\n" +
            "@attribute $PF250 numeric\n" +
            "@attribute $PF251 numeric\n" +
            "@attribute $PF252 numeric\n" +
            "@attribute $PF253 numeric\n" +
            "@attribute $PF254 numeric\n" +
            "@attribute $PF255 numeric\n" +
            "@attribute $PF256 numeric\n" +
            "@attribute $PF257 numeric\n" +
            "@attribute $PF258 numeric\n" +
            "@attribute $PF259 numeric\n" +
            "\n" +
            "@attribute @@class@@ {pos,neg}\n" +
            "\n" +
            "@data\n";

    public PastebinPreFilter(){
        try {
            sclassifier = new SerializedClassifier();
            sclassifier.setModelFile(new File("./src/main/resources/PF.model"));
            //classifier = (RandomForest) sclassifier;
        } catch (Exception e) {
            e.printStackTrace();
        }

        codeWordsPatternList = new ArrayList<>();
        gameWordsPatternList = new ArrayList<>();
        sportsWordsPatternList = new ArrayList<>();
        pornWordsPatternList = new ArrayList<>();
        greetingsWordsPatternList = new ArrayList<>();

        codeWordsList = new ArrayList(Arrays.asList("abstract","boolean","break","byte","case","catch","char","continue","default","do","double","else","extends","finally","float","for","if","implements","import","instanceof","int","interface","long","new","package","private","protected","public","return","short","static","super","switch","synchronized","this","throw|throws","void","volatile","while",
                "and","bool","compl","#define","delete","exit","false","namespace","not","operator","or","sizeof","struct","true","xor","as","foreach","null","object","override","using","<html>|</html>|html","<head>|</head>|head","<title>|</title>|title","<body>|</body>|body","<h1>|</h1>|<h2>|</h2>|<h3>|</h3>|<h4>|</h4>|<h5>|</h5>|<h6>|</h6>","<img>|</img>","<link>|link","<br>","<a>|</a>","<p>|</p>","<style>|</style>",
                "array","declare","echo","elseif","empty","function","global","include","include_once","insteadof","isset","require","require_once","use","var","lambda","none","del","elif"));
        gameWordsList = new ArrayList(Arrays.asList("Wolfenstein","game|games","The New Colossus","Assassin's Creed","Middle-earth","Shadow of War","Destiny","Call of Duty","Dishonored","Death of the Outsider","Dusk","Lawbreakers","Vanquish","PlayerUnknown's Battlegrounds","Friday the 13th","Game","The Signal From Tolva","Ghost Recon","Wildlands","Prey","Resident Evil","Biohazard","Bulletstorm","Sniper Elite ","Strafe","Desync","Rising Storm","Sea of Thieves","Metal Gear Survive","World at war","Black ops","Ghosts","Warfare","Xbox one"));
        sportsWordsList = new ArrayList(Arrays.asList("arena","athlete|Athletics" ,"badminton","ball","base","baseball","basketball","bat","boxing","bronze medal|gold medal|silver medal","competitor","crew","Cricket","field|fielder|fielding\n","Gym|gymnast|gymnastics|gymnasium","goal","goalie","Olympics","Paintball","race|racer|racing","Racket","relay","Ride|riding","rugby","Run|runner|running","Swim|swimmer|swimming","table tennis|tennis","taekwondo","Team|teammate","tetherball","Throw,throwing","Umpire","volley ball","Weightlifter|weightlifting|weights","Rafting","winner|winning","World Cup|World Series","Wrestler|wrestling","Surfing","Sports\n"));
        pornWordsList = new ArrayList(Arrays.asList("Intercouse","Loved|lover|love|loves","Kiss","Hug|hugs","Womb","Virgin","Homo|homo sexual","Vagina","Gay","Lesbian","Sex","Seduce","Rape,rapist","Erection|erectile|erect|erotic","Pubic","Dick","Prostitute|prostate","Cuddle","Genital","Pregnant","Condom|condoms","Butt|butts","Penis","Breast|breasts","Nipple","Aroused","porn","Naked|nude","Lust","Makeout","Abortion","Fingering","Horny","Orgasm","Ass|anus|anal","Boob","Fuck|fucked|fucking|fucker","Tit|tits","Cocks|cock","Pussy","Slut","Pornography","Foreplay"));
        greetingsWordsList = new ArrayList(Arrays.asList("Blessings","Greetings","Gratitude","Celebrate,celebration","Happiness|joy|pleasure|laughter","Health","Peace","Prosperity","Season","Rejoice","Success|fortune","Wishes|best wishes","New year|coming year","Chritmas","Good luck","fantastic")); // remove good luck

        for(String word:codeWordsList){
            codeWordsPatternList.add(Pattern.compile(word,Pattern.CASE_INSENSITIVE));
        }

        for(String word:gameWordsList){
            gameWordsPatternList.add(Pattern.compile(word,Pattern.CASE_INSENSITIVE));
        }

        for(String word:sportsWordsList){
            sportsWordsPatternList.add(Pattern.compile(word,Pattern.CASE_INSENSITIVE));
        }

        for(String word:pornWordsList){
            pornWordsPatternList.add(Pattern.compile(word,Pattern.CASE_INSENSITIVE));
        }

        for(String word:greetingsWordsList){
            greetingsWordsPatternList.add(Pattern.compile(word,Pattern.CASE_INSENSITIVE));
        }

    }

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

        keyWordList = new ArrayList<String>();
        keyWordList.add("game");
        keyWordList.add("sports");
        keyWordList.add("porn");
        keyWordList.add("sex");
        keyWordList.add("xxx");
    }

    public void execute(Tuple tuple) {

        Post post = (Post)tuple.getValue(0);

        // Convert the pastebin post to the lower case
        post.setPostText(post.getPostText().toLowerCase());

        //if pre filter is passed forward the model to next bolt(context filter)
        if(!isContainKeyWord(post.getPostText())) {
            collector.emit(tuple, new Values(post));
        }else{
//            System.out.println("\nUser: " + post.getUser() + "\nTitle: " + post.getTitle() + "\n" + post.getPostText() + "\n--- Filtered out by pre filter ---\n");
        }
        collector.ack(tuple);

    }

    private boolean isContainKeyWord(String post) {

        try {

            for (int i=0;i<keyWordList.size();i++) {
                if (post.contains(keyWordList.get(i).toString())) {
                    //exit after the first successful hit
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}