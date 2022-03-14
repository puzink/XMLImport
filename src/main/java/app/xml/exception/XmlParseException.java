package app.xml.exception;

import app.xml.CursorPosition;

import java.io.IOException;


//TODO refactor
public class XmlParseException extends IOException {

    //TODO add cursor logic
    private CursorPosition position;

    public XmlParseException(String message){
        super(message);
    }

    public XmlParseException(CursorPosition cursorPosition){
        this(null, cursorPosition);
    }

    public XmlParseException(String message, CursorPosition position) {
        super(message);
        this.position = position;
    }

}
