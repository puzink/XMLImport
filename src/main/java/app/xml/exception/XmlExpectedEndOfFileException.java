package app.xml.exception;

import app.xml.CursorPosition;

public class XmlExpectedEndOfFileException extends XmlParseException {


    public XmlExpectedEndOfFileException(CursorPosition cursorPosition){
        super(cursorPosition);
    }
    public XmlExpectedEndOfFileException(String message, CursorPosition position) {
        super(message, position);
    }
}
