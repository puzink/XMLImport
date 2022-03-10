package app.xml;

import app.xml.exception.XmlParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TestParseValidExamples {

    private ClassLoader classLoader = TestParseNotValidExamples.class.getClassLoader();
    private String validXmlsDirectory = "xmls/valids/";
    private XmlTagParser tagParser = new XmlTagParserImpl();

    private List<Node> readAllNodes(File xml) throws IOException{
        try(XmlParserImpl parser = new XmlParserImpl(xml, tagParser)) {
            List<Node> res = new ArrayList<>();
            Node node;
            while (parser.hasNextNode()) {
                node = parser.getNextNode();
                res.add(node);
            }
            return res;
        }
    }

    private File getXmlFileForName(String fileName) throws URISyntaxException {;
        URI testDirectory = classLoader.getResource("").toURI();
        return new File(testDirectory.resolve(validXmlsDirectory).resolve(fileName));
    }

    @Test
    public void testValidTest() throws IOException, URISyntaxException{
        File xml = getXmlFileForName("double_close_tag.xml");

        Node table = new Node(null, new Tag("table",List.of(),TagType.OPEN));
        table.appendIntoBody(">" + System.lineSeparator());
        Node row = new Node(table, new Tag("row", List.of(), TagType.OPEN));
        Node id = new Node(row, new Tag("id", List.of(), TagType.OPEN));
        id.appendIntoBody("2");
        Node name = new Node(row, new Tag("name", List.of(), TagType.OPEN));
        name.appendIntoBody("Ole >g");
        List<Node> expected = List.of(table,row,id,name);

        List<Node> actual = readAllNodes(xml);

        Assertions.assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); ++i){
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }

    }

    @Test
    public void testSpacesAfterLineInCloseTag() throws IOException, URISyntaxException{
        File xml = getXmlFileForName("spaces_after_line_in_close_tag.xml");

        Node table = new Node(
                null,
                new Tag("table",List.of(new Attribute("name", "users")), TagType.OPEN)
        );
        Node row = new Node(table, new Tag("row", List.of(), TagType.OPEN));
        Node id = new Node(row, new Tag("id", List.of(), TagType.OPEN));
        id.appendIntoBody("2");
        Node name = new Node(row, new Tag("name", List.of(), TagType.OPEN));
        name.appendIntoBody("Oleg");
        List<Node> expected = List.of(table,row,id,name);

        List<Node> actual = readAllNodes(xml);

        Assertions.assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); ++i){
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }

    }

    @Test
    public void testSeveralRows() throws URISyntaxException, IOException{
        File xml = getXmlFileForName("several_rows.xml");

        Node table = new Node(
                null,
                new Tag("table",List.of(new Attribute("users", "321")), TagType.OPEN)
        );
        Node row1 = new Node(table,
                new Tag("row", List.of(new Attribute("val", "4561safd'")), TagType.OPEN)
        );
        Node name1 = new Node(row1, new Tag("name", List.of(), TagType.OPEN));
        name1.appendIntoBody("Oleg");
        Node row2 = new Node(table,
                new Tag("row", List.of(new Attribute("id", "65")), TagType.OPEN)
        );
        Node name2 = new Node(row2, new Tag("name", List.of(), TagType.OPEN));
        name2.appendIntoBody("abcd");
        Node str = new Node(row2, new Tag("str", List.of(), TagType.OPEN));
        str.appendIntoBody("qwerty");
        List<Node> expected = List.of(table,row1,name1,row2,name2,str);

        List<Node> actual = readAllNodes(xml);

        Assertions.assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); ++i){
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }
}
