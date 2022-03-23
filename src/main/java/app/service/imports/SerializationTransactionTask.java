package app.service.imports;

import app.utils.ThreadTransactionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public abstract class SerializationTransactionTask<T> extends TransactionalTask<T> {

    private static final String SERIALIZATION_FAILURE = "40001";

    public SerializationTransactionTask(ThreadTransactionManager tx) {
        super(tx, Connection.TRANSACTION_SERIALIZABLE);
    }

    @Override
    public boolean isRecoverable(SQLException e){
        return Objects.equals(SERIALIZATION_FAILURE, e.getSQLState());
    }

}
