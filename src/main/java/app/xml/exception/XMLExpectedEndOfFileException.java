package app.xml.exception;

import app.xml.CursorPosition;
import app.xml.XMLFileParser;
import app.xml.XmlTagParser;

import java.io.File;
import java.io.IOException;

public class XMLExpectedEndOfFileException extends XmlParseException {


    public XMLExpectedEndOfFileException(CursorPosition cursorPosition){
        super(cursorPosition);
    }
    public XMLExpectedEndOfFileException(String message, CursorPosition position) {
        super(message, position);
    }
}
