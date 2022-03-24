package app.transaction;

import java.sql.SQLException;

/**
 * Управляет транзакциями в {@link java.sql.Connection}.
 */
public interface ThreadTransactionManager {

    /**
     * Начинает транзакцию в {@link java.sql.Connection}, ставя {@code Connection.setAutoCommit(false)}
     * @param isolationLevel
     * @throws SQLException
     */
    void begin(int isolationLevel) throws SQLException;
    void commit() throws SQLException;
    void rollback() throws SQLException;
    void close() throws SQLException;

}
