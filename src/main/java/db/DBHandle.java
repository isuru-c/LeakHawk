/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

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
