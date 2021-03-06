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

import bolt.core.LeakHawkBolt;
import model.Post;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import util.LeakHawkConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This bolt is used to process urls within posts
 * <p>
 * When evidence test is passed for any post, the post is checked if it contains
 * any url in the post body. If so, those set of urls are added to a list is
 * Post data structure and emit the post to UrlProcessor instead of emitting to the
 * content classifier.
 * <p>
 * After the set of urls examined, the posts are put to their original flow again.
 * [To the pastebin posts flow or tweets flow]
 *
 * @author Isuru Chandima
 */
public class UrlProcessor extends LeakHawkBolt {

    private Pattern urlPattern;

    @Override
    public void prepareBolt() {
//        urlPattern = Pattern.compile(
//                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
//                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
//                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
//                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        urlPattern = Pattern.compile("(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    }

    @Override
    public void execute(Tuple tuple) {
        Post post = (Post) tuple.getValue(0);

        Matcher matcher = urlPattern.matcher(post.getPostText());

        boolean urlFound = false;

        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();

            String url = post.getPostText().substring(matchStart, matchEnd);
            post.addUrl(url);
            urlFound = true;
        }

        if (urlFound) {
            // Url is found within the post.
            increaseOutCount();

            ArrayList<String> urlList = post.getUrlList();

            StringBuffer urlContent = new StringBuffer();

            for (String url : urlList) {

                try {
                    URL url1 = new URL(url);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url1.openStream()));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        urlContent.append(inputLine);

                    in.close();
                } catch (MalformedURLException e) {

                } catch (IOException e) {

                }
            }

            if(!urlContent.toString().isEmpty()){
                post.setUrlContent(urlContent.toString());
                post.setUrlContentFound(true);
            }

        }

        if (post.getPostType().equals(LeakHawkConstant.POST_TYPE_PASTEBIN)) {
            collector.emit(LeakHawkConstant.URL_PROCESSOR_TO_P_CONTENT_CLASSIFIER, tuple, new Values(post));
        } else if (post.getPostType().equals(LeakHawkConstant.POST_TYPE_TWEETS)) {
            collector.emit(LeakHawkConstant.URL_PROCESSOR_TO_T_CONTENT_CLASSIFIER, tuple, new Values(post));
        }
    }

    @Override
    protected String getBoltName() {
        return LeakHawkConstant.URL_PROCESSOR;
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkConstant.URL_PROCESSOR_TO_P_CONTENT_CLASSIFIER);
        outputStream.add(LeakHawkConstant.URL_PROCESSOR_TO_T_CONTENT_CLASSIFIER);

        return outputStream;
    }
}
