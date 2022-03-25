package app;

import app.dao.RowDao;
import app.dao.RowDaoImpl;
import app.dao.TableDaoImpl;
import app.jdbc.*;
import app.repository.RowRepositoryImpl;
import app.repository.TableRepositoryImpl;
import app.imports.XmlImporter;
import app.imports.transaction.ThreadConnectionPool;
import app.imports.transaction.ThreadConnectionTransactionManagerImpl;
import app.xml.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/test";
        String user = "postgres";
        String pass = "chronit53142";
        return DriverManager.getConnection(url, user, pass);
    }

    private static ThreadConnectionPool getConnectionPool(){
        String url = "jdbc:postgresql://localhost:5432/test";
        String user = "postgres";
        String pass = "chronit53142";
        return new ThreadConnectionPool(user, pass, url);
    }

    private static File getFile() throws URISyntaxException {
        URI testDirectory = Main.class.getClassLoader().getResource("").toURI();
        return new File(testDirectory.resolve("table.xml"));
    }


    private static ThreadPoolExecutor createExecutor() {
        int queueSize = 20;
        int minThreads = 0;
        int maxThreads = 3;
        if(System.getProperty("threads") != null){
            maxThreads = Integer.parseInt(System.getProperty("threads"));
        }

        return new ThreadPoolExecutor(minThreads, maxThreads,
                0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static void main(String[] args) throws Exception {
        ThreadConnectionPool connectionPool = getConnectionPool();
        ThreadConnectionTransactionManagerImpl tx = new ThreadConnectionTransactionManagerImpl(connectionPool);

        RowDao simpleRowDao = new RowDaoImpl(connectionPool);
        TableDaoImpl simpleTableDao = new TableDaoImpl(connectionPool);


        File file = new File("C:\\Users\\puzink\\Documents\\testImporter\\" + "biTableWithItems.txt");

        RowRepositoryImpl repository = new RowRepositoryImpl(connectionPool, simpleRowDao);
        TableRepositoryImpl tableRepository = new TableRepositoryImpl(connectionPool, simpleTableDao);
        XmlParser parser = new XmlLazyParser(file, new XmlElementParserImpl());
        XmlTableReader tableReader = new XmlTableReaderImpl(parser);
        ThreadPoolExecutor executor = createExecutor();
        try{
            XmlImporter xmlImporter =
                    new XmlImporter(repository, tableRepository, tx);
            long start = System.currentTimeMillis();
            System.out.println(xmlImporter.importUniqueTableRows(tableReader));
            System.out.println("Time = " + (System.currentTimeMillis() - start)/1000);
            System.out.println();
        } finally {
            executor.shutdown();
        }


    }
}
