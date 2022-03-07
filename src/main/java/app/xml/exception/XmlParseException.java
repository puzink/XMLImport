package app.xml.exception;

import java.io.IOException;

public class XmlParseException extends IOException {
    public XmlParseException() {
    }

    public XmlParseException(String message) {
        super(message);
    }

    public XmlParseException(Throwable cause) {
        super(cause);
    }
}
