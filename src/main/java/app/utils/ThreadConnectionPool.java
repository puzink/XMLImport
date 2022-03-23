package app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;


public class ThreadConnectionPool {

    private final HashMap<Long, Connection> connectionPool;
    private final String user;
    private final String pass;
    private final String url;

    public ThreadConnectionPool(String username, String pass, String url) {
        this.connectionPool = new HashMap<>();
        this.user = username;
        this.pass = pass;
        this.url = url;
    }

    public boolean isConnectionActive(){
        return connectionPool.containsKey(Thread.currentThread().getId());
    }

    public Connection getConnection() throws SQLException {
        Long threadId = Thread.currentThread().getId();
        if(connectionPool.containsKey(threadId)){
           return connectionPool.get(threadId);
        }
        Connection conn = DriverManager.getConnection(url,user,pass);
        connectionPool.put(threadId, conn);
        return conn;
    }

    public void closeConnection() throws SQLException {
        Long threadId = Thread.currentThread().getId();
        if(connectionPool.containsKey(threadId)){
            connectionPool.get(threadId).close();
            connectionPool.remove(threadId);
        }
    }

    public void close() throws SQLException {
        for(Map.Entry<Long, Connection> entry : connectionPool.entrySet()){
            entry.getValue().close();
        }
    }
}
