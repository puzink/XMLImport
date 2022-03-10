package app.xml;

import java.io.IOException;

//TODO change name
public interface XmlParser {

    boolean hasNextNode() throws IOException;
    Node getNextNode() throws IOException;

}
