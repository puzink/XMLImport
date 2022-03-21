package app;

import app.jdbc.RowDao;
import app.repository.RowRepository;
import app.jdbc.TableDao;
import app.repository.TableRepository;
import app.service.imports.XmlImporter;
import app.xml.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/test";
        String user = "postgres";
        String pass = "chronit53142";
        return DriverManager.getConnection(url, user, pass);
    }

    private static File getFile() throws URISyntaxException {
        URI testDirectory = Main.class.getClassLoader().getResource("").toURI();
        return new File(testDirectory.resolve("table.xml"));
    }


    public static void main(String[] args) throws Exception {
        RowDao rowDao = new RowDao(getConnection());
        TableDao tableDao = new TableDao(getConnection());
        File file = new File("C:\\Users\\puzink\\Documents\\testImporter\\" + "tableWith20Rows.txt");
        RowRepository repository = new RowRepository(rowDao);
        TableRepository tableRepository = new TableRepository(tableDao);
//        File file = getFile();
        try(XmlParser parser = new XmlLazyParser(file, new XmlElementParserImpl())){
            XmlTableReaderImpl tableReader = new XmlTableReaderImpl(parser);
            XmlImporter xmlImporter = new XmlImporter(repository, tableRepository);
            long start = System.nanoTime();
            System.out.println(xmlImporter.importUniqueTableRows(tableReader));
            System.out.println("Time = " + (System.nanoTime() - start));
        }


    }
}
