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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Sugeesh Chandraweera
 */
public class DBConnection {
    static private DBConnection dbConnection;
    static private Connection connection;

    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost/LeakHawk", "root", "");
    }

    public Connection getConnection() {
        return connection;
    }

    public static DBConnection getDBConnection() throws ClassNotFoundException, SQLException {
        //     if(dbConnection==null){
        dbConnection = new DBConnection();
        //   }
        return dbConnection;
    }

    /**
     * Closes database connection
     */
    public static void closeDBConnection() {
        try {
            if (connection != null)
                connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
