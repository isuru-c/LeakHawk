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
import util.LeakHawkParameters;

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
        urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
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

        }

        if (post.getPostType().equals(LeakHawkParameters.POST_TYPE_PASTEBIN)) {
            collector.emit(LeakHawkParameters.URL_PROCESSOR_TO_P_CONTENT_CLASSIFIER, tuple, new Values(post));
        } else if (post.getPostType().equals(LeakHawkParameters.POST_TYPE_TWEETS)) {
            collector.emit(LeakHawkParameters.URL_PROCESSOR_TO_T_CONTENT_CLASSIFIER, tuple, new Values(post));
        }
    }

    @Override
    protected String getBoltName() {
        return LeakHawkParameters.URL_PROCESSOR;
    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        outputStream.add(LeakHawkParameters.URL_PROCESSOR_TO_P_CONTENT_CLASSIFIER);
        outputStream.add(LeakHawkParameters.URL_PROCESSOR_TO_T_CONTENT_CLASSIFIER);

        return outputStream;
    }
}
