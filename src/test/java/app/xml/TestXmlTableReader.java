package app.xml;

import app.model.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestXmlTableReader {

    @Test
    public void testReadRows() throws IOException{
        Element tableOpen = new Element("table", new ArrayList<>(), ElementType.OPEN);
        Element row1Open = new Element("row", new ArrayList<>(), ElementType.OPEN);
        Element idOpen = new Element("id", new ArrayList<>(), ElementType.OPEN);
        Element idClose = new Element("id", new ArrayList<>(), ElementType.CLOSE);
        Element nameOpen = new Element("name", new ArrayList<>(), ElementType.OPEN);
        Element nameClose = new Element("name", new ArrayList<>(), ElementType.CLOSE);
        Element row1Close = new Element("row", new ArrayList<>(), ElementType.CLOSE);
        Element row2Open = new Element("row", new ArrayList<>(), ElementType.OPEN);
        Element cityOpen = new Element("city", new ArrayList<>(), ElementType.OPEN);
        Element cityClose = new Element("city", new ArrayList<>(), ElementType.CLOSE);
        Element phoneOpen = new Element("phone", new ArrayList<>(), ElementType.OPEN);
        Element phoneClose = new Element("phone", new ArrayList<>(), ElementType.CLOSE);
        Element row2Close = new Element("row", new ArrayList<>(), ElementType.CLOSE);
        Element tableClose = new Element("table", new ArrayList<>(), ElementType.CLOSE);

        List<String> nodeBodies = List.of("","","243","Oleg","","New-York","8534891");

        List<Element> elements = List.of(tableOpen, row1Open, idOpen, idClose, nameOpen, nameClose, row1Close,
                row2Open, cityOpen, cityClose, phoneOpen, phoneClose, row2Close, tableClose);

        XmlParser parser = new ListParser(elements, nodeBodies);

        TableReader tableReader = new XmlTableReader(parser);
        Row expectedRow = new Row(Map.of("id","243", "name", "Oleg"));
        Row expectedRow2 = new Row(Map.of("city","New-York", "phone", "8534891"));

        Assertions.assertEquals(expectedRow, tableReader.readRow());
        Assertions.assertEquals(expectedRow2, tableReader.readRow());
        Assertions.assertNull(tableReader.readRow());
        Assertions.assertNull(tableReader.readRow());

    }

    @Test
    public void testReadNotRowNode() throws IOException{

        Element tableOpen = new Element("table", new ArrayList<>(), ElementType.OPEN);
        Element idOpen = new Element("id", new ArrayList<>(), ElementType.OPEN);

        XmlParser parser = new ListParser(List.of(tableOpen, idOpen), List.of("",""));

        TableReader tableReader = new XmlTableReader(parser);

        Assertions.assertThrows(IllegalArgumentException.class,
                tableReader::readRow,
                "A non-row element was encountered in the table"
        );

    }


}
