package app.xml;

import app.xml.exception.XmlTagParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestTagParse {

    private XmlTagParser tagParser = new XmlTagParserImpl();

    @Test
    public void testInvalidAttributeNames(){
        String attributeWithValue = "3name = \"rewd\"";
        Assertions.assertThrows(XmlTagParseException.class,
                ()-> tagParser.parseAttributes(attributeWithValue),
                "Name must start with letter."
        );

        String unexpectedSymbolInName = "na]me = \"rewd\"";
        Assertions.assertThrows(XmlTagParseException.class,
                ()-> tagParser.parseAttributes(unexpectedSymbolInName),
                "Name must contain digits and letters only."
        );

        String emptyName = "= \"rewd\"";
        Assertions.assertThrows(XmlTagParseException.class,
                ()-> tagParser.parseAttributes(emptyName),
                "Name must not be empty."
        );

        String notClosedValue = "name = \"val";
        Assertions.assertThrows(XmlTagParseException.class,
                ()-> tagParser.parseAttributes(notClosedValue),
                "Attribute value is not closed."
        );
    }

    @Test
    public void testValidAttributeNames() throws XmlTagParseException {
        String nameWithDigits = "na32me = \"rewd\"";
        Assertions.assertEquals("na32me", tagParser.parseName(nameWithDigits));

        String oneLetterName = "n = \"rewd\"";
        Assertions.assertEquals("n", tagParser.parseName(oneLetterName));
    }

    @Test
    public void testAttributeParsing() throws XmlTagParseException {
        String str = "name1 = \"val1\" name2 = \"2\"";
        List<Attribute> expected =
                List.of(new Attribute("name1", "val1"), new Attribute("name2", "2"));
        List<Attribute> actual = tagParser.parseAttributes(str);
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertArrayEquals(expected.toArray(), tagParser.parseAttributes(str).toArray());
    }

    @Test
    public void testEmptyAttributes() throws XmlTagParseException {
        String str = "    \t\n";
        Assertions.assertTrue(tagParser.parseAttributes(str).isEmpty());
    }


}
