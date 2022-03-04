package app.xml;

import lombok.Getter;

@Getter
public class Node {
    private final Element element;
    private final StringBuilder body;
    private final Node prev;

    public Node(Node prev, String element) {
        this(prev, new Element(element));
    }

    public Node(Node prev, Element element){
        this.prev = prev;
        this.element = element;
        body = new StringBuilder();
    }

    public String getName(){
        return element.getName();
    }

    public void appendIntoBody(char c){
        body.append(c);
    }

    public String toString(){
        return String.format("Name:%s, elem = '%s', body='%s'", element.getName(), element, body.toString());
    }

    public boolean isBodyEmpty() {
        return body.length() == 0;
    }
}
