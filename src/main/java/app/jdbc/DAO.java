package app.jdbc;

import app.model.Row;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class DAO {

    private Connection conn;

    public DAO(String url, String user, String password) throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    public int insertLines(Collection<Row> rows, String table){


        return 0;
    }

}
