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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import backtype.storm.tuple.Tuple;

/**
 *
 * @author Sugeesh Chandraweera
 * @author sewwandi
 */
public class DBHandle {
    /**
     * Database connection
     */
    private Connection connection;

    public DBHandle(){
        try {
            connection = DBConnection.getDBConnection().getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int insertData(Tuple tuple,String sensitivityLevel,int content,int evidence,String predictClass){
        PreparedStatement statement=null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        int result;

        try{
            statement = connection.prepareStatement("insert into Incident (post_key,user,title,type,date,sensivityLevel,content,evidence,predictClass) values (?,?,?,?,?,?,?,?,?)");
            statement.setString(0, tuple.getString(0));
            statement.setString(1, tuple.getString(1));
            statement.setString(2, tuple.getString(2));
            statement.setString(3, tuple.getString(3));
            statement.setString(4, dateFormat.format(date));
            statement.setString(5, sensitivityLevel);
            statement.setString(6, content);
            statement.setString(7, evidence);
            statement.setString(8, predictClass);

            result=statement.executeUpdate();
        }catch(Exception exception)
        {
            exception.printStackTrace();
        }

        return result;
    }

    public static int setData(Connection connection,String sql) throws SQLException{
        Statement statement=connection.createStatement();
        int result=statement.executeUpdate(sql);
        return result;
    }

    public static ResultSet getData(Connection connection,String sql) throws SQLException{
        Statement statement=connection.createStatement();
        ResultSet result=statement.executeQuery(sql);
        return result;
    }

    /*public static void close()
    {
        connection.closeDBConnection();
    }*/

}
