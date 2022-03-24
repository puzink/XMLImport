package app.repository;

import app.transaction.ThreadConnectionPool;

public abstract class AbstractRepository implements Repository{
    protected final ThreadConnectionPool connectionPool;

    protected AbstractRepository(ThreadConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
