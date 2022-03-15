package app.xml.exception;

import app.xml.CursorPosition;

public class XmlEndOfFileException extends XmlParseException{

    private static String message = "Reached the end of the file.";

    public XmlEndOfFileException(CursorPosition cursorPosition) {
        super(cursorPosition);
    }
}
