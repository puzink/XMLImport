package app.xml;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Node {
    private final Element element;
    private final StringBuilder body;
    @Setter
    private NodeStatus status = NodeStatus.CLOSED;
    private final Node parent;

    public Node(Node parent, Element element, NodeStatus status){
        this(parent, element);
        this.status = status;
    }

//    public Node(Node parent, String element) {
//        this(parent, new Element(element));
//    }

    public Node(Node parent, Element element){
        this.parent = parent;
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

    public String getText(){
        return body.toString();
    }

    public boolean isOpened() {
        return status == NodeStatus.OPENED;
    }
}
