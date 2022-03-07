package app.xml;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Node {
    private final Tag tag;
    private final StringBuilder body;
    @Setter
    private NodeStatus status = NodeStatus.CLOSED;
    private final Node parent;

    public Node(Node parent, Tag tag, NodeStatus status){
        this(parent, tag);
        this.status = status;
    }
    
    public Node(Node parent, Tag tag){
        this.parent = parent;
        this.tag = tag;
        body = new StringBuilder();
    }

    public String getName(){
        return tag.getName();
    }

    public void appendIntoBody(char c){
        body.append(c);
    }

    public String toString(){
        return String.format("Name:%s, elem = '%s', body='%s'", tag.getName(), tag, body.toString());
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
