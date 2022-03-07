package app.xml;

import java.util.LinkedList;

public class NodePath extends LinkedList<Node>{

    public Node getTailNode(){
        if(isEmpty()){
            return null;
        }
        return getLast();
    }

    public void appendIntoBody(char c) {
        if(isEmpty()){
            throw new IllegalArgumentException("Unexpected symbol occurs.");
        }
        getLast().appendIntoBody(c);
    }

}
