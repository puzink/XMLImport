package app.xml.exception;

import app.xml.CursorPosition;

public class XmlTagParseException extends XmlParseException{

    //TODO change
    public XmlTagParseException(String message) {
        super(message, null);
    }

    public XmlTagParseException(String message, CursorPosition cursorPosition) {
        super(message, cursorPosition);
    }
}
