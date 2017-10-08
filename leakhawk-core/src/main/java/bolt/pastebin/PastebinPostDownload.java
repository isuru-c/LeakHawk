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
package bolt.pastebin;

import bolt.core.LeakHawkUtility;
import exception.LeakHawkDataStreamException;
import exception.LeakHawkFilePathException;
import exception.LeakHawkTopologyException;
import model.Post;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.LeakHawkParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * When requesting for new posts from pastebin scrape_url, it only gives the set of keys
 * of latest post. Later it needs to fetch each post using its unique key by requesting
 * again using the scrape_url and unique key.
 *
 * Fetching each post and encapsulate contents of each post in a Post object is the
 * task of this bolt. Only pastebin flow needs to go through this bolt.
 *
 * @author Isuru Chandima
 */
public class PastebinPostDownload extends LeakHawkUtility {

    private JSONParser parser = null;

    @Override
    public void prepareUtility() {
        parser = new JSONParser();
    }

    @Override
    public void executeUtility(Tuple tuple, OutputCollector collector) {
        try {

            Object obj = parser.parse(tuple.getString(0));
            JSONObject postDetails = (JSONObject) obj;
            Post post = new Post();
            String postUrl = (String) postDetails.get("scrape_url");

            post.setPostType(LeakHawkParameters.POST_TYPE_PASTEBIN);
            post.setKey((String) postDetails.get("key"));
            post.setDate((String) postDetails.get("date"));
            post.setTitle((String) postDetails.get("title"));
            post.setUser((String) postDetails.get("user"));
            post.setSyntax((String) postDetails.get("syntax"));

            String postText = "";
            URL my_url2 = new URL(postUrl);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(my_url2.openStream()));
            while (bufferedReader.ready()) {
                postText += bufferedReader.readLine();
            }
            post.setPostText(postText);
            collector.emit(tuple, new Values(post));
        } catch (ParseException e) {
            throw new LeakHawkTopologyException("Posts cannot reach PatebinPostDownload Bolt.",e);
        } catch (MalformedURLException e) {
            throw new LeakHawkDataStreamException("Pastebin Post Download failed, Provided URL is broken",e);
        } catch (IOException e) {
            throw new LeakHawkDataStreamException("Pastebin Post Download failed, Paste reading failed.",e);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("post"));
    }
}
