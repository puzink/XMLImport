package app.jdbc;

import app.model.Line;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DAO {

    private Connection conn;

    public DAO(String url, String user, String password) throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    public int insertLines(Collection<Line> lines, String table){


        return 0;
    }

}
