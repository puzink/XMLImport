package app.utils;

import lombok.Getter;

import java.sql.Connection;

@Getter
public class Transaction {

    private final ThreadTransactionManager tx;
    private Transaction parent;
    private final Connection connection;

    public Transaction(ThreadTransactionManager tx, Transaction parent, Connection connection) {
        this.tx = tx;
        this.parent = parent;
        this.connection = connection;
    }
}
