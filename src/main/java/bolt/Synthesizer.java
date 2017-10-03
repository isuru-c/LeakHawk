/*
 * Copyright 2017 SWIS
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

import model.ContentData;
import model.EvidenceModel;
import model.ContentModel;
import model.Post;
import db.DBConnection;
import db.DBHandle;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import parameters.LeakHawkParameters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to predict the sensitive level and risk of a post by using
 * classification done by previous content and evidence classifiers
 *
 * @author Isuru Chandima
 * @author Sugeesh Chandraweera
 */
public class Synthesizer extends BaseRichBolt {

    private OutputCollector collector;
    private EvidenceModel evidenceModel;
    private ContentModel contentModel;
    private Connection connection;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Tuple tuple) {

        Post post = (Post) tuple.getValue(0);

        if (post.getPostType().equals(LeakHawkParameters.postTypePastebin)) {
            synthesizePastebinPosts(post);
        } else if (post.getPostType().equals(LeakHawkParameters.postTypeTweets)) {
            synthesizeTweets(post);
        }

        collector.ack(tuple);
    }

    public void synthesizePastebinPosts(Post post){
        evidenceModel = post.getEvidenceModel();
        contentModel = post.getContentModel();

        if (contentModel.isContentFound()) {
            List contentDataList = contentModel.getContentDataList();
            int highestLevel = 0;

            for (Object contentDataObj : contentDataList) {
                ContentData contentData = (ContentData) contentDataObj;
                if (contentData.getLevel() > highestLevel) {
                   highestLevel = contentData.getLevel();
                }
            }

            ArrayList<ContentData> highestContent = new ArrayList<>();
            for (Object contentDataObj : contentDataList) {
                ContentData contentData = (ContentData) contentDataObj;
                if (contentData.getLevel() == highestLevel) {
                    highestContent.add(contentData);
                }
            }

            String classString = "";
            for (int i = 0; i < highestContent.size(); i++) {
                classString += highestContent.get(i).getContentType();
                if (i != highestContent.size() - 1) {
                    classString += ",";
                }
            }

            if (evidenceModel.isEvidenceFound() && highestLevel > 0) {
                String title = post.getTitle().replace("'", "/'");
                String user = post.getUser().replace("'", "/'");
                try {
                    DBHandle.setData(connection, "INSERT INTO Incident VALUES ('" + post.getKey() + "','" + user + "','" + title + "','"
                            + post.getPostType() + "','" + post.getDate() + "'," + highestLevel + "," + contentModel.isContentFound()
                            + "," + evidenceModel.isEvidenceFound() + ",'" + classString + "')");

                    System.out.println("\nPost  : " + post.getKey());
                    System.out.println("\nEvidence Found  : " + evidenceModel.isEvidenceFound());
                    System.out.println("\nContent Found  : " + contentModel.isContentFound());
                    System.out.println("Sensitivity level of post is :" + highestLevel + "\n");
                    System.out.println("Sensitivity class is  :" + classString + "\n");

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void synthesizeTweets(Post post){


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("no-no"));
    }
}



