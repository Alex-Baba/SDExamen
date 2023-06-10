package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConn {

    private Connection conn;
    private static SQLiteConn instance = null;

    private SQLiteConn(){
        String url = "jdbc:sqlite:/home/student/SD/repo-classroom/l1/tema-acasa/studentdb.db";

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConn(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLiteConn getInstance() {
        if(instance == null)
            instance = new SQLiteConn();
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}
