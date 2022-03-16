package app.xml.exception;

import app.xml.CursorPosition;

public class XmlUnexpectedElementMetException extends XmlParseException{

    public XmlUnexpectedElementMetException(CursorPosition cursorPosition) {
        super(cursorPosition);
    }

    public XmlUnexpectedElementMetException(String message) {
        super(message);
    }
}
