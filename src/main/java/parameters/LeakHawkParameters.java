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

package parameters;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to store global variables of the LeakHawk
 *
 * @author Isuru Chandima
 */
public class LeakHawkParameters {

    // These keywords are used to define the topics from and into kafka broker
    public static String postTypePastebin = "pastebin-posts";
    public static String postTypeTweets = "tweets";

    // Store the keyword list for the twitter pre filter
    public static ArrayList<String> twitterPreFilterKeywordList;
    public static boolean twitterPreFilterKeywordListSet = false;

    /**
     * Read the file TwitterPreFilterList and create a array list for the pre filter
     *
     * @return Array List of strings containing keyword list for the twitter pre filter
     */
    public static ArrayList<String> getTwitterPreFilterKeywordList() {

        if (twitterPreFilterKeywordListSet) {
            return twitterPreFilterKeywordList;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream("./src/main/resources/TwitterPreFilterList.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                String strLine;
                ArrayList<String> keywordList = new ArrayList<String>();

                while ((strLine = bufferedReader.readLine()) != null) {
                    keywordList.add(strLine);
                }

                bufferedReader.close();

                twitterPreFilterKeywordList = keywordList;
                twitterPreFilterKeywordListSet = true;

                return twitterPreFilterKeywordList;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return twitterPreFilterKeywordList;
    }
}
