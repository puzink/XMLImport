package app.xml;

import app.xml.exception.XmlNoMoreNodesException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ListParser implements XmlParser {

    private final List<Node> nodes;
    private Iterator<Node> it;

    public ListParser(List<Node> nodes){
        this.nodes = nodes;
        this.it = nodes.iterator();
    }

    @Override
    public boolean hasNextNode() throws IOException {
        return it.hasNext();
    }

    @Override
    public Node getNextNode() throws IOException {
        if(!hasNextNode()){
            throw new XmlNoMoreNodesException("There is no more nodes.", null);
        }
        return it.next();
    }

    @Override
    public void close() throws Exception {
        throw new UnsupportedOperationException("Cannot close list.");
    }
}
