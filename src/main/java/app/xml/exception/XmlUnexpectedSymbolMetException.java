package app.xml.exception;

import app.xml.CursorPosition;

public class XmlUnexpectedSymbolMetException extends XmlParseException {

    private static String messagePattern = "Unexpected symbol occurs: %s.";

    public XmlUnexpectedSymbolMetException(char c) {
        super(String.format(messagePattern, Character.valueOf(c).toString()));
    }

    public XmlUnexpectedSymbolMetException(CursorPosition cursorPosition) {
        super(cursorPosition);
    }

    public XmlUnexpectedSymbolMetException(char c, CursorPosition position) {
        super(String.format(messagePattern, Character.valueOf(c).toString()), position);
    }

    public XmlUnexpectedSymbolMetException(String message, CursorPosition position){
        super(message, position);
    }
}
