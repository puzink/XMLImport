package app;

import app.jdbc.DAO;
import app.service.Importer;
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
        DAO dao = new DAO(getConnection());
        File file = new File("C:\\Users\\puzink\\Documents\\testImporter\\" + "tableWith20Rows.txt");
//        File file = getFile();
        try(XmlParser parser = new XmlLazyParser(file, new XmlElementParserImpl())){
            XmlTableReaderImpl tableReader = new XmlTableReaderImpl(parser);
            Importer importer = new Importer(dao, tableReader);
            long start = System.nanoTime();
            System.out.println(importer.importRows());
            System.out.println("Time = " + (System.nanoTime() - start));
        }


    }
}
