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

package util;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to store global variables of the LeakHawk
 *
 * @author Isuru Chandima
 */
public class LeakHawkParameters {

    // These keywords are used to define the topics from and into kafka broker
    public static String POST_TYPE_PASTEBIN = "pastebin-posts";
    public static String POST_TYPE_TWEETS = "tweets";
    public static String POST_TYPE_DUMP = "dump-posts";

    // Path for the dump post folder
    public  static String DUMP_FOLDER_PATH = "./posts";

    // Parameters needs to connect to the twitter stream API
    public static String CONSUMER_KEY = "Qrk3fZ04WaW0Qw0zVE7MSwYNi";
    public static String CONSUMER_SECRET = "9jXaU9kTDHh2pLGDyQc69AI9YhHmj2Huf2AbYcaWKgE8M3Jmzy";
    public static String TOKEN = "1627974024-AmWhRjy2pThPIpc1nwEhTmhws1U0AYPHkukUZrc";
    public static String TOKEN_SECRET = "HC7Vq3VSsOLuQ1QjZ3NihpwCymWi00pbvT10kelCtS29t";

    // Pastebin scraping URL and post limit per request
    public static String PASTEBIN_SCAPING_URL = "http://pastebin.com/api_scraping.php?limit=";
    public static int PASTEBIN_POST_LIMIT = 100;

    // Pastebin sensor sleep time
    public static int PASTEBIN_SENSOR_SLEEP_TIME = 10000;

    // Store the keyword list for the twitter pre filter
    public static ArrayList<String> TWITTER_PRE_FILTER_KEYWORD_LIST;

    public static boolean TWITTER_PRE_FILTER_KEYWORD_LIST_SET = false;

    /**
     * Read the file TwitterPreFilterList and create a array list for the pre filter
     *
     * @return Array List of strings containing keyword list for the twitter pre filter
     */
    public ArrayList<String> getTwitterPreFilterKeywordList() {

        if (TWITTER_PRE_FILTER_KEYWORD_LIST_SET) {
            return TWITTER_PRE_FILTER_KEYWORD_LIST;
        } else {
            try {
                InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("TwitterPreFilterList.txt");
//                FileInputStream fileInputStream = new FileInputStream("./src/main/resources/TwitterPreFilterList.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                String strLine;
                ArrayList<String> keywordList = new ArrayList<String>();

                while ((strLine = bufferedReader.readLine()) != null) {
                    keywordList.add(strLine);
                }

                bufferedReader.close();

                TWITTER_PRE_FILTER_KEYWORD_LIST = keywordList;
                TWITTER_PRE_FILTER_KEYWORD_LIST_SET = true;

                return TWITTER_PRE_FILTER_KEYWORD_LIST;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return TWITTER_PRE_FILTER_KEYWORD_LIST;
    }
}
