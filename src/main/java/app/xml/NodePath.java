package app.xml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NodePath{

    private final List<Node> nodes;

    public NodePath(List<Node> nodes) {
        this.nodes = nodes;
    }

    public NodePath(Node... nodes){
        this.nodes = List.of(nodes);
    }

    public NodePath(NodePath nodePath){
        this.nodes = List.copyOf(nodePath.nodes);
    }

    public NodePath addNode(Node node){
        List<Node> newPath = new ArrayList<>(nodes);
        newPath.add(node);
        return new NodePath(newPath);
    }

    public static NodePath pathOf(List<Node> nodes){
        return new NodePath(nodes);
    }

    public void appendIntoBody(char c) {
        if(isEmpty()){
            throw new IllegalArgumentException("Unexpected symbol occurs.");
        }
        getTailNode().appendIntoBody(c);
    }

    public Node getTailNode(){
        if(isEmpty()){
            return null;
        }
        return nodes.get(nodes.size() - 1);
    }

    public boolean isEmpty(){
        return nodes.isEmpty();
    }

    public NodePath removeLast() {
        return new NodePath(nodes.subList(0, nodes.size()-1));
    }
}
