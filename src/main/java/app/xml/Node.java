package app.xml;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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
    public void appendIntoBody(String s){
        body.append(s);
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

    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(o.getClass() != this.getClass()){
            return false;
        }
        Node other = (Node) o;
        return Objects.equals(other.getParent(), parent)
                && Objects.equals(body.toString(), other.getBody().toString())
                && Objects.equals(tag, other.getTag());

    }
}
