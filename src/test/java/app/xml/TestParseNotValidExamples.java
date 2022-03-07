package app.xml;

import app.xml.exception.XmlParseException;
import app.xml.exception.XmlTagParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TestParseNotValidExamples {

    private ClassLoader classLoader = TestParseNotValidExamples.class.getClassLoader();
    private String notValidXmlsDirectory = "xmls/withErrors/";
    private XmlTagParser tagParser = new XmlTagParserImpl();

    private List<Node> readAllNodes(File xml) throws IOException, XmlParseException {
        try(XMLFileParser parser = new XMLFileParser(xml, tagParser)) {
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
        return new File(testDirectory.resolve(notValidXmlsDirectory).resolve(fileName));
    }

    @Test
    public void testDoubleOpenTag() throws IOException, URISyntaxException {
        String xmlName = "double_open_tag.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlParseException.class,
                () -> readAllNodes(xml),
                "Double open tag."
        );
    }

    @Test
    public void testCloseTagBeforeOpen() throws URISyntaxException {
        String xmlName = "close_tag_before_open.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlParseException.class,
                () -> readAllNodes(xml),
                "Close tag before the open one."
        );
    }

    @Test
    public void testEndOfFileInTag() throws URISyntaxException {
        String xmlName = "end_of_file_inside_tag.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlParseException.class,
                () -> readAllNodes(xml),
                "Unexpected file end."
        );
    }

    @Test
    public void testLeadingSpacesInCloseTag() throws URISyntaxException {
        String xmlName = "leading_spaces_in_close_tag.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Whitespaces before tag name."
        );
    }

    @Test
    public void testLeadingSpacesInTag() throws URISyntaxException {
        String xmlName = "leading_spaces_in_tag.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Whitespaces before tag name."
        );
    }

    @Test
    public void testUnexpectedSymbolBeforeRoot() throws URISyntaxException {
        String xmlName = "unexpected_symbol_before_root.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlParseException.class,
                () -> readAllNodes(xml),
                "Unexpected symbol occurs."
        );
    }

    @Test
    public void testSymbolAfterEnd() throws URISyntaxException {
        String xmlName = "symbols_after_end.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlParseException.class,
                () -> readAllNodes(xml),
                "Unexpected symbol occurs."
        );
    }

    @Test
    public void testTagAfterXmlEnd() throws URISyntaxException {
        String xmlName = "tag_after_xml_end.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlParseException.class,
                () -> readAllNodes(xml),
               "Multiply root tags."
        );
    }

    @Test
    public void testCloseTagWithoutName() throws URISyntaxException {
        String xmlName = "close_tag_without_name.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Wrong tag name."
        );
    }

    @Test
    public void testAttributeWithoutEqualSign() throws URISyntaxException {
        String xmlName = "attribute_without_equal_sign.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Attribute must has a value."
        );
    }

    @Test
    public void testAttributeWithoutValue() throws URISyntaxException {
        String xmlName = "attribute_without_value.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Attribute must has a value."
        );
    }

    @Test
    public void testAttributeWithoutKey() throws URISyntaxException {
        String xmlName = "attribute_without_key.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Attribute name must not be empty."
        );
    }

    @Test
    public void testAttributeValueWithoutQuotes() throws URISyntaxException {
        String xmlName = "attribute_value_without_quotes.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Attribute value must be enclosed in double quotes."
        );
    }

    @Test
    public void testTagNameWithEqualSign() throws URISyntaxException {
        String xmlName = "tag_name_with_equal_sign.xml";
        File xml = getXmlFileForName(xmlName);

        Assertions.assertThrows(
                XmlTagParseException.class,
                () -> readAllNodes(xml),
                "Name must contain digits and letters only."
        );
    }
}
