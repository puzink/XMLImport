package app.xml;

import app.model.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestTableRowsReader {

    @Test
    public void testReadRows() throws IOException{
        Node table = new Node(null, new Tag("table",null, TagType.OPEN),NodeStatus.OPENED);
        Node row1 = new Node(table, new Tag("row",null, TagType.OPEN),NodeStatus.OPENED);
        Node id = new Node(row1, new Tag("id",null, TagType.OPEN),NodeStatus.OPENED);
        id.appendIntoBody("243");
        Node name = new Node(row1, new Tag("name",null, TagType.OPEN),NodeStatus.OPENED);
        name.appendIntoBody("Oleg");
        Node row2 = new Node(table, new Tag("row",null, TagType.OPEN),NodeStatus.OPENED);
        Node city = new Node(row2, new Tag("city",null, TagType.OPEN),NodeStatus.OPENED);
        city.appendIntoBody("New-York");
        Node phone = new Node(row2, new Tag("phone",null, TagType.OPEN),NodeStatus.OPENED);
        phone.appendIntoBody("8534891");

        List<Node> nodes = List.of(table, row1, id, name, row2, city, phone);

        XmlParser parser = new ListParser(nodes);

        TableReader tableReader = new TableRowsReader(parser);
        Row expectedRow = new Row(Map.of("id","243", "name", "Oleg"));
        Row expectedRow2 = new Row(Map.of("city","New-York", "phone", "8534891"));

        Assertions.assertEquals(expectedRow, tableReader.readRow());
        Assertions.assertEquals(expectedRow2, tableReader.readRow());
        Assertions.assertNull(tableReader.readRow());
        Assertions.assertNull(tableReader.readRow());

    }

    @Test
    public void testReadNotRowNode() throws IOException{
        Node table = new Node(null, new Tag("table",null, TagType.OPEN),NodeStatus.OPENED);
        Node id = new Node(table, new Tag("id",null, TagType.OPEN),NodeStatus.OPENED);
        List<Node> nodes = List.of(table, id);

        XmlParser parser = new ListParser(nodes);

        TableReader tableReader = new TableRowsReader(parser);

        Assertions.assertThrows(IllegalArgumentException.class,
                tableReader::readRow,
                "A non-row element was encountered in the table");

    }


}
