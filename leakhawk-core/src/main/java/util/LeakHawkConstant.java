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

/**
 * This class is used to store global variables of the LeakHawk
 *
 * @author Isuru Chandima
 */
public class LeakHawkConstant {

    // These keywords are used to define the topics from and into kafka broker
    public static String POST_TYPE_PASTEBIN = "pastebin-posts";
    public static String POST_TYPE_TWEETS = "tweets";
    public static String POST_TYPE_DUMP = "dump-posts";

    // Identifiers for different classifiers and filters
    public static String PASTEBIN_PRE_FILTER = "pastebin-pre-filter";
    public static String PASTEBIN_EVIDENCE_CLASSIFIER = "pastebin-evidence-classifier";
    public static String PASTEBIN_CONTENT_CLASSIFIER = "pastebin-content-classifier";

    public static String CONTEXT_FILTER = "context-filter";

    public static String TWEETS_PRE_FILTER = "tweets-pre-filter";
    public static String TWEETS_EVIDENCE_CLASSIFIER = "tweets-evidence-classifier";
    public static String TWEETS_CONTENT_CLASSIFIER = "tweets-content-classifier";

    public static String SYNTHESIZER = "synthesizer";

    public static String URL_PROCESSOR = "url-processor";
    public static String STATICS_COUNTER = "statics-counter";
    public static String POST_DOWNLOADER = "post-downloader";

    // Time interval for statics updates
    public static long STATICS_UPDATE_INTERVAL = 10;

    public static String STATICS_FLOW = "statics-flow";

    // Identifiers for streams between bolts
    public static String DUMP_SPOUT_TO_P_PRE_FILTER = "ds-ppf";
    public static String DUMP_SPOUT_TO_T_PRE_FILTER = "ds-tpf";

    public static String P_POST_DOWNLOADER_TO_P_PRE_FILTER = "ppd-ppf";

    public static String P_PRE_FILTER_TO_CONTEXT_FILTER = "ppf-cf";
    public static String T_PRE_FILTER_TO_CONTEXT_FILTER = "tpf-cf";

    public static String CONTEXT_FILTER_TO_P_EVIDENCE_CLASSIFIER = "cf-pec";
    public static String CONTEXT_FILTER_TO_T_EVIDENCE_CLASSIFIER = "cf-tec";

    public static String P_EVIDENCE_CLASSIFIER_TO_P_CONTENT_CLASSIFIER = "pec-pcc";
    public static String P_EVIDENCE_CLASSIFIER_TO_URL_PROCESSOR = "pec-url";
    public static String URL_PROCESSOR_TO_P_CONTENT_CLASSIFIER = "url-pcc";

    public static String T_EVIDENCE_CLASSIFIER_TO_T_CONTENT_CLASSIFIER = "tec-tcc";
    public static String T_EVIDENCE_CLASSIFIER_TO_URL_PROCESSOR = "tec-url";
    public static String URL_PROCESSOR_TO_T_CONTENT_CLASSIFIER = "url-tcc";

    public static String P_CONTENT_CLASSIFIER_TO_SYNTHESIZER = "pcc-s";
    public static String T_CONTENT_CLASSIFIER_TO_SYNTHESIZER = "tcc-s";

    // Path for the dump post folder
    public  static String DUMP_FOLDER_PATH = "./posts";

    // Parameters needs to connect to the twitter stream API
    public static String CONSUMER_KEY = "Qrk3fZ04WaW0Qw0zVE7MSwYNi";
    public static String CONSUMER_SECRET = "9jXaU9kTDHh2pLGDyQc69AI9YhHmj2Huf2AbYcaWKgE8M3Jmzy";
    public static String TOKEN = "1627974024-AmWhRjy2pThPIpc1nwEhTmhws1U0AYPHkukUZrc";
    public static String TOKEN_SECRET = "HC7Vq3VSsOLuQ1QjZ3NihpwCymWi00pbvT10kelCtS29t";

    // Pastebin scraping URL and post limit per request
    public static String PASTEBIN_SCRAPING_URL = "http://pastebin.com/api_scraping.php?limit=";
    public static int PASTEBIN_POST_LIMIT = 100;

    // Pastebin sensor sleep time
    public static int PASTEBIN_SENSOR_SLEEP_TIME = 10000;

    //contentFilterFilePath
    public static String CONTEXT_FILTER_FILE_PATH = "/home/neo/Desktop/MyFYP/Project/LeakHawk2.0/LeakHawk/leakhawk-core/src/main/resources/context.properties";
    public static String RESOURCE_FOLDER_FILE_PATH = "/home/neo/Desktop/MyFYP/Project/LeakHawk2.0/LeakHawk/leakhawk-core/src/main/resources";

    // Twitter pre-filter file paths
    public static String TWITTER_PRE_FILTER_FILE_PATH = "/home/neo/Desktop/MyFYP/Project/LeakHawk2.0/LeakHawk/leakhawk-core/src/main/resources/TwitterPreFilterList.txt";


}
