package app.xml.exception;

import app.xml.CursorPosition;

public class XmlElementParseException extends XmlParseException{

    //TODO change
    public XmlElementParseException(String message) {
        super(message, null);
    }

    public XmlElementParseException(String message, CursorPosition cursorPosition) {
        super(message, cursorPosition);
    }
}
