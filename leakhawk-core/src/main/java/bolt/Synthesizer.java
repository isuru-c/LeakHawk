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

import bolt.core.LeakHawkClassifier;
import exception.LeakHawkDatabaseException;
import model.ContentData;
import model.EvidenceModel;
import model.ContentModel;
import model.Post;
import db.DBConnection;
import db.DBHandle;
import util.LeakHawkParameters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to predict the sensitive level and risk of a post by using
 * classification done by previous content and evidence classifiers
 *
 * @author Isuru Chandima
 * @author Sugeesh Chandraweera
 */
public class Synthesizer extends LeakHawkClassifier {

    private EvidenceModel evidenceModel;
    private ContentModel contentModel;
    private Connection connection;

    @Override
    public void prepareClassifier() {
        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            throw new LeakHawkDatabaseException("DBConnection class not found", e);
        } catch (SQLException e) {
            throw new LeakHawkDatabaseException("Database Connection Error. Please check username and password", e);
        }
    }

    @Override
    protected String getBoltName() {
        return LeakHawkParameters.SYNTHESIZER;
    }

    @Override
    public void classifyPost(Post post) {

        // Set next output stream to be null, so there will be no more forwarding
        post.setNextOutputStream(null);

        if (post.getPostType().equals(LeakHawkParameters.POST_TYPE_PASTEBIN)) {
            synthesizePastebinPosts(post);
        } else if (post.getPostType().equals(LeakHawkParameters.POST_TYPE_DUMP)) {
            synthesizeTweets(post);
        }
    }

    private void synthesizePastebinPosts(Post post){
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
//                    DBHandle.setData(connection, "INSERT INTO incident VALUES ('" + post.getKey() + "','" + user + "','" + title + "','"
//                            + post.getPostType() + "','" + post.getDate() + "'," + highestLevel + "," + contentModel.isContentFound()
//                            + "," + evidenceModel.isEvidenceFound() + ",'" + classString + "')");

                    DBHandle.setData(connection, "INSERT INTO incident VALUES ('" + post.getKey() + "'" +
                            ","+ contentModel.isContentFound()+",'"+post.getDate()+"',"+evidenceModel.isEvidenceFound()+"" +
                            ",'"+classString+"',"+highestLevel+",'"+post.getTitle()+"','"+post.getPostType()+"','"+post.getUser()+"')");

                    System.out.println("\nPost  : " + post.getKey());
                    System.out.println("\nEvidence Found  : " + evidenceModel.isEvidenceFound());
                    System.out.println("\nContent Found  : " + contentModel.isContentFound());
                    System.out.println("Sensitivity level of post is :" + highestLevel + "\n");
                    System.out.println("Sensitivity class is  :" + classString + "\n");

                } catch (SQLException e) {
                    throw new LeakHawkDatabaseException("Database Insertion Failed on Synthesizer.",e);
                }
            }
        }
    }

    private void synthesizeTweets(Post post){
        evidenceModel = post.getEvidenceModel();
        contentModel = post.getContentModel();
        String classString = "";

        if (contentModel.isContentFound()) {
            List contentDataList = contentModel.getContentDataList();
            int i=1;

            for (Object contentDataObj : contentDataList) {
                ContentData contentData = (ContentData) contentDataObj;
                classString+=contentData.getContentType();
                if(contentDataList.size()>i){
                    classString += ",";
                }
                i++;
            }

            if (evidenceModel.isEvidenceFound()) {
                String title = post.getTitle().replace("'", "/'");
                String user = post.getUser().replace("'", "/'");
                try {
                    DBHandle.setData(connection, "INSERT INTO Incident VALUES ('" + post.getKey() + "','" + user + "','" + title + "','"
                            + post.getPostType() + "','" + post.getDate() + "'," + 0 + "," + contentModel.isContentFound()
                            + "," + evidenceModel.isEvidenceFound() + ",'" + classString + "')");

                    System.out.println("\nPost  : " + post.getKey());
                    System.out.println("\nEvidence Found  : " + evidenceModel.isEvidenceFound());
                    System.out.println("\nContent Found  : " + contentModel.isContentFound());
                    System.out.println("Sensitivity class is  :" + classString + "\n");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        return outputStream;
    }
}



