package app.xml;

import app.model.Row;
import app.model.Table;
import app.xml.exception.XmlParseException;

import java.io.IOException;

public interface TableReader {

    Node getTable() throws IOException;
    Row readRow() throws IOException;

}
