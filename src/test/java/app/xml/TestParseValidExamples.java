package app.xml;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TestParseValidExamples {

    @Test
    public void testValidTest() throws IOException {
        File xml = new File(TestParseValidExamples.class.getClassLoader().getResource("xmls/test.xml").getFile());
        XMLFileParser parser = new XMLFileParser(xml);

        Node node;
        while((node = parser.getNextNode()) != null){
            System.out.println(node);
        }

    }
}
