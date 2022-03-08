package app.xml;

import app.model.Row;

import java.io.IOException;

public interface TableReader {

    Node getTable() throws IOException;
    Row readRow() throws IOException;

}
