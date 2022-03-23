package app.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadTransactionManagerImpl implements ThreadTransactionManager {

    private final ThreadConnectionPool connectionPool;
    private final ConcurrentHashMap<Long, Transaction> transactions = new ConcurrentHashMap<>();

    public ThreadTransactionManagerImpl(ThreadConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void begin(int isolationLevel) throws SQLException {
        Long threadId = Thread.currentThread().getId();
        Transaction currentTransaction = transactions.get(threadId);
        Transaction newTransaction = null;
        if(currentTransaction != null){
            newTransaction = new Transaction(this, currentTransaction, currentTransaction.getConnection());
        } else {
            Connection connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);
            newTransaction = new Transaction(this, null, connection);
        }

        transactions.put(threadId, newTransaction);
    }

    public void commit() throws SQLException {
        Long threadId = Thread.currentThread().getId();
        Transaction transaction = transactions.get(threadId);
        if(transaction == null){
            throw new NullPointerException("Transaction is not exists.");
        }

        if(transaction.getParent() != null){
            transactions.put(threadId, transaction.getParent());
        } else {
            transactions.remove(threadId);
            Connection conn = transaction.getConnection();
            conn.commit();
            connectionPool.closeConnection();
        }
    }

    @Override
    public void rollback() throws SQLException {
        Long threadId = Thread.currentThread().getId();
        Transaction transaction = transactions.get(threadId);
        transaction.getConnection().rollback();
        if(transaction.getParent() == null){
            transactions.remove(threadId);
        } else {
            transactions.put(threadId, transaction.getParent());
        }
    }

    @Override
    public void close() throws SQLException{
        connectionPool.closeConnection();
    }

}
