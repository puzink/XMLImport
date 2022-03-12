package app;

import app.jdbc.DAO;
import app.service.Service;
import app.xml.*;

import java.io.File;
import java.io.IOException;
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


    public static void main(String[] args) throws SQLException, URISyntaxException, IOException {
        DAO dao = new DAO(getConnection());
        File file = getFile();
        XmlParser parser = new XmlParserImpl(file, new XmlTagParserImpl());
        TableReader tableReader = new XmlTableReader(parser);

        Service service = new Service(dao, tableReader);

        System.out.println(service.importRows());
        System.out.println();

    }
}
