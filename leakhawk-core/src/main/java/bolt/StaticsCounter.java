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
import db.DBConnection;
import db.DBHandle;
import exception.LeakHawkDatabaseException;
import model.Statics;
import org.apache.storm.tuple.Tuple;
import util.LeakHawkConstant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * This bolt is used to collect the statics of posts processed in different nodes
 * in the LeakHawk topology. All the statics are emitted from different nodes and
 * are collected from this bolt and do the necessary processing for them.
 * <p>
 * Main objective is to write statics to the database so the LeakHawk monitor can
 * later collect those statics.
 *
 * @author Isuru Chandima
 */
public class StaticsCounter extends LeakHawkBolt {

    private Connection connection;

    @Override
    public void prepareBolt() {

        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            throw new LeakHawkDatabaseException("DBConnection class not found", e);
        } catch (SQLException e) {
            throw new LeakHawkDatabaseException("Database Connection Error. Please check username and password", e);
        }
        // Create a database connection in here as required
    }

    @Override
    public void execute(Tuple tuple) {
        Statics statics = (Statics) tuple.getValue(0);

        try {
            if (LeakHawkConstant.PASTEBIN_PRE_FILTER.equals(statics.getBoltType()) || LeakHawkConstant.TWEETS_PRE_FILTER.equals(statics.getBoltType())) {
                ResultSet data = DBHandle.getData(connection, "SELECT value from chart_detail where field_name='totalPostCount'");
                int currentTotalPosts = 0;
                int currentPreFilterPassedCount = 0;
                if (data.next()) {
                    currentTotalPosts = data.getInt(1);
                }
                ResultSet data1 = DBHandle.getData(connection, "SELECT value from chart_detail where field_name='preFilterPassedCount'");
                if (data1.next()) {
                    currentPreFilterPassedCount = data1.getInt(1);
                }


                String updateTotalPosts = "UPDATE chart_detail SET value = ? where field_name = 'totalPostCount'";
                PreparedStatement preparedStatement1 = connection.prepareStatement(updateTotalPosts);
                preparedStatement1.setInt(1, (currentTotalPosts + statics.getInCount()));
                preparedStatement1.executeUpdate();


                String updatePreFilterPassed = "UPDATE chart_detail SET value = ? where field_name='preFilterPassedCount'";
                PreparedStatement preparedStatement2 = connection.prepareStatement(updatePreFilterPassed);
                preparedStatement2.setInt(1, (currentPreFilterPassedCount + statics.getOutCount()));
                preparedStatement2.executeUpdate();


            } else if (LeakHawkConstant.CONTEXT_FILTER.equals(statics.getBoltType())) {
                ResultSet data = DBHandle.getData(connection, "SELECT value from chart_detail where field_name='contextFilterPassedCount'");
                int currentPreFilterPassedCount = 0;
                if (data.next()) {
                    currentPreFilterPassedCount = data.getInt(1);
                }

                String updateContextFilterPassed = "UPDATE chart_detail SET value = ? where field_name='contextFilterPassedCount'";
                PreparedStatement preparedStatement1 = connection.prepareStatement(updateContextFilterPassed);
                preparedStatement1.setInt(1, (currentPreFilterPassedCount + statics.getOutCount()));
                preparedStatement1.executeUpdate();

            } else if (LeakHawkConstant.PASTEBIN_CONTENT_CLASSIFIER.equals(statics.getBoltType()) || LeakHawkConstant.TWEETS_CONTENT_CLASSIFIER.equals(statics.getBoltType())) {
                ResultSet data = DBHandle.getData(connection, "SELECT value from chart_detail where field_name='contentPassedCount'");
                int contentPassedCount = 0;
                if (data.next()) {
                    contentPassedCount = data.getInt(1);
                }

                String updateContentPassed = "UPDATE chart_detail SET value = ? where field_name='contentPassedCount'";
                PreparedStatement preparedStatement1 = connection.prepareStatement(updateContentPassed);
                preparedStatement1.setInt(1, (contentPassedCount + statics.getOutCount()));
                preparedStatement1.executeUpdate();

            } else if (LeakHawkConstant.PASTEBIN_EVIDENCE_CLASSIFIER.equals(statics.getBoltType()) || LeakHawkConstant.TWEETS_EVIDENCE_CLASSIFIER.equals(statics.getBoltType())) {
                ResultSet data = DBHandle.getData(connection, "SELECT value from chart_detail where field_name='evidencePassedCount'");
                int evidencePassedCount = 0;
                if (data.next()) {
                    evidencePassedCount = data.getInt(1);
                }

                String updateEvidencePassed = "UPDATE chart_detail SET value = ? where field_name='evidencePassedCount'";
                PreparedStatement preparedStatement1 = connection.prepareStatement(updateEvidencePassed);
                preparedStatement1.setInt(1, (evidencePassedCount + statics.getOutCount()) );
                preparedStatement1.executeUpdate();
            }
            System.out.println("Updated Chart details.");
        } catch (SQLException e) {
            throw new LeakHawkDatabaseException("Database statistic update failed", e);
        }
        System.out.println(statics.getBoltType() + ": in - " + statics.getInCount() + " out - " + statics.getOutCount());
    }

    @Override
    protected String getBoltName() {
        return LeakHawkConstant.STATICS_COUNTER;
    }


    @Override
    public ArrayList<String> declareOutputStreams() {
        ArrayList<String> outputStream = new ArrayList<>();

        return outputStream;
    }
}
