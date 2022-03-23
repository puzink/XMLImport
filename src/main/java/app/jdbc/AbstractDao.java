package app.jdbc;

import app.utils.ThreadConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDao implements Dao {

    protected final ThreadConnectionPool connectionPool;

    public AbstractDao(ThreadConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }

    protected Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

}
