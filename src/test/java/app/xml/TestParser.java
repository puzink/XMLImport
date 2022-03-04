package app.xml;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TestParser {

    @Test
    public void testValidTest() throws IOException {
        File xml = new File(TestParser.class.getClassLoader().getResource("xmls/test.xml").getFile());
        XMLFileParser parser = new XMLFileParser(xml);

        Node node;
        while((node = parser.findNextNode()) != null){
            System.out.println(node);
        }

    }
}
