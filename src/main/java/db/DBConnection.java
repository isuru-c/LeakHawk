/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Sugeesh Chandraweera
 */
public class DBConnection {
    static private DBConnection dbConnection;
    static private Connection connection;

    private DBConnection() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        connection=DriverManager.getConnection("jdbc:mysql://localhost/LeakHawk","root","");
    }

    public Connection getConnection(){
        return connection;
    }

    public static DBConnection getDBConnection() throws ClassNotFoundException, SQLException{
   //     if(dbConnection==null){
            dbConnection=new DBConnection();
     //   }
        return dbConnection;
    }

    
    
}
