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
package core.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Sugeesh Chandraweera
 */
public class DBHandle {
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

}
