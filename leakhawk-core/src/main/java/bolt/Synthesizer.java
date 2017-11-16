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
import util.LeakHawkConstant;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        return LeakHawkConstant.SYNTHESIZER;
    }

    @Override
    public void classifyPost(Post post) {

        // Set next output stream to be null, so there will be no more forwarding
        post.setNextOutputStream(null);

        if (post.getPostType().equals(LeakHawkConstant.POST_TYPE_PASTEBIN)) {
            synthesizePosts(post);
        } else if (post.getPostType().equals(LeakHawkConstant.POST_TYPE_TWEETS)) {
            synthesizePosts(post);
        } else if (post.getPostType().equals(LeakHawkConstant.POST_TYPE_DUMP)) {
            synthesizePosts(post);
        }
    }

    private void synthesizePosts(Post post){
        evidenceModel = post.getEvidenceModel();
        contentModel = post.getContentModel();

        if (contentModel.isContentFound()) {
            List contentDataList = contentModel.getContentDataList();
            int highestLevel = getHighestSensitivityLevel(contentDataList);
            List<ContentData> highestContent = getAllHighestLevelContent(contentDataList,highestLevel);
            String classString = getHighestLevelDetails(highestContent);

            if (evidenceModel.isEvidenceFound()) {
                writeIncidentToDatabase(post,highestLevel,classString);
            }else if(highestLevel>=2){
                writeIncidentToDatabase(post,highestLevel,classString);
            }
        }else if(evidenceModel.isEvidenceFound()){
            writeIncidentToDatabase(post,0,"No Content Found");
        }

    }

//    private void synthesizeTweets(Post post){
//        evidenceModel = post.getEvidenceModel();
//        contentModel = post.getContentModel();
//        String classString = "";
//
//
//        if (contentModel.isContentFound()) {
//            List contentDataList = contentModel.getContentDataList();
//            int i=1;
//
//            for (Object contentDataObj : contentDataList) {
//                ContentData contentData = (ContentData) contentDataObj;
//                classString+=contentData.getContentType();
//                if(contentDataList.size()>i){
//                    classString += ",";
//                }
//                i++;
//            }
//
//            if (evidenceModel.isEvidenceFound()) {
//                writeIncidentToDatabase(post,0, classString);
//            }
//        }
//
//    }

    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        return outputStream;
    }


    /**
     * This method will write the sensitive incidents to the database
     * @param post post object of the incident
     * @param highestLevel highest level of sensitivity
     * @param classString class of the sensitivity
     */
    private void writeIncidentToDatabase(Post post, int highestLevel, String classString){
        String title = "";
        String user = "";
        if(!title.isEmpty())
            title = post.getTitle().replace("'", "/'");
        if(!user.isEmpty())
            user = post.getUser().replace("'", "/'");
        try {
            String sql = "INSERT INTO incident (post_key,content,date,evidence,predict_class,sensitivity_level,title,type,user,link) VALUES (?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt=connection.prepareStatement(sql);
            String postDate = post.getDate();
            if("tweets".equals(post.getPostType())){
                final String TWITTER="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
                SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
                postDate = Long.toString(sf.parse(post.getDate()).getTime());
            }else{
                postDate = post.getDate()+"000";
            }

            stmt.setString(1,post.getKey());
            stmt.setBoolean(2,contentModel.isContentFound());
            stmt.setString(3,postDate);
            stmt.setBoolean(4,evidenceModel.isEvidenceFound());
            stmt.setString(5,classString);
            stmt.setInt(6,highestLevel);
            stmt.setString(7,post.getTitle());
            stmt.setString(8,post.getPostType());
            stmt.setString(9,post.getUser());
            stmt.setBoolean(10,post.isUrlContentFound());

            stmt.executeUpdate();

            System.out.println("\nPost  : " + post.getKey());
            System.out.println("\nEvidence Found  : " + evidenceModel.isEvidenceFound());
            System.out.println("\nContent Found  : " + contentModel.isContentFound());
            System.out.println("Sensitivity level of post is :" + highestLevel + "\n");
            System.out.println("Sensitivity class is  :" + classString + "\n");

        } catch (SQLException e) {
            throw new LeakHawkDatabaseException("Database Insertion Failed on Synthesizer.",e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private int getHighestSensitivityLevel(List contentDataList){
        int highestLevel = 0;
        for (Object contentDataObj : contentDataList) {
            ContentData contentData = (ContentData) contentDataObj;
            if (contentData.getLevel() > highestLevel) {
                highestLevel = contentData.getLevel();
            }
        }
        return highestLevel;
    }


    private List<ContentData> getAllHighestLevelContent(List contentDataList, int highestLevel){
        ArrayList<ContentData> highestContent = new ArrayList<>();
        for (Object contentDataObj : contentDataList) {
            ContentData contentData = (ContentData) contentDataObj;
            if (contentData.getLevel() == highestLevel) {
                highestContent.add(contentData);
            }
        }
        return highestContent;
    }

    private String getHighestLevelDetails(List<ContentData> highestContent){
        String classString = "";
        for (int i = 0; i < highestContent.size(); i++) {
            classString += highestContent.get(i).getContentType();
            if (i != highestContent.size() - 1) {
                classString += ",";
            }
        }
        return classString;
    }
}



