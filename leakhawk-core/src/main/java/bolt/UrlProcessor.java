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

import bolt.core.LeakHawkUtility;
import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import parameters.LeakHawkParameters;

import java.util.Map;
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
public class UrlProcessor extends LeakHawkUtility {

    private String pastebinOut = "url-processor-pastebin-out";
    private String tweetsOut = "url-processor-tweets-out";

    @Override
    public void prepareUtility() {

    }

    @Override
    public void executeUtility(Tuple tuple, OutputCollector collector) {
        Post post = (Post) tuple.getValue(0);

        Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

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

        }

        if (post.getPostType().equals(LeakHawkParameters.postTypePastebin) || post.getPostType().equals(LeakHawkParameters.postTypeDump)) {
            collector.emit(pastebinOut, tuple, new Values(post));
        } else if (post.getPostType().equals(LeakHawkParameters.postTypeTweets)) {
            collector.emit(tweetsOut, tuple, new Values(post));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(pastebinOut, new Fields("post"));
        outputFieldsDeclarer.declareStream(tweetsOut, new Fields("post"));
    }
}
