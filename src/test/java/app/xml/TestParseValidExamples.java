package app.xml;

import app.xml.exception.XmlParseException;
import app.xml.exception.XmlTagParseException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TestParseValidExamples {
    private XmlTagParser elementParser = new XmlTagParserImpl();


    @Test
    public void testValidTest() throws IOException, XmlParseException {
        File xml = new File(TestParseValidExamples.class.getClassLoader().getResource("xmls/test.xml").getFile());
        XMLFileParser parser = new XMLFileParser(xml, elementParser);

        Node node;
        while((node = parser.getNextNode()) != null){
            System.out.println(node);
        }

    }
}
