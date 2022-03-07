package app.xml;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class XMLFileParser implements AutoCloseable{

    private final File file;
    private final BufferedReader buffIn;

    private final NodePath nodePath = new NodePath();
    private Node nextNode = null;
    private boolean mainElementIsFound = false;

    public XMLFileParser(File file) throws IOException {
        this.file = file;

        FileInputStream fileInput = new FileInputStream(file);
        FileChannel channel =fileInput.getChannel();
        buffIn = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));

        nextNode = findNextNode();
    }

    public Node getNextNode() throws IOException{
        if(!hasNextNode()){
            throw new IllegalArgumentException("There is no more nodes in the file.");
        }

        Node currentNode = nextNode;
        nextNode = findNextNode();
        return currentNode;

    }

    public boolean hasNextNode() {
        return nextNode != null;
    }

    private Node findNextNode() throws IOException {
        Element element = getNextElement();
        if(checkElementClose(element)){
            nodePath.getTailNode().setStatus(NodeStatus.CLOSED);
            nodePath.removeLast();
            return findNextNode();
        }

        Node newNode = new Node(nodePath.getTailNode(), element, NodeStatus.OPENED);
        nodePath.add(newNode);
        return newNode;
    }

    private Element getNextElement() throws IOException {
        readCharsBeforeNextElement();
        return new Element(readElement());
    }

    private void readCharsBeforeNextElement() throws IOException {
        int c;
        while((c = readChar()) != '<'){
            char ch = (char) c;
            if(nodePath.isEmpty() && !Character.isWhitespace(ch)){
                throw new IllegalArgumentException("Unexpected symbol occurs.");
            }

            if(!nodePath.isEmpty()
                    && !nodePath.getTailNode().isBodyEmpty()){
                nodePath.appendIntoBody(ch);
            }
        }
    }

    private String readElement() throws IOException {
        StringBuilder element = new StringBuilder();
        int c;
        while((c = readChar()) != '>'){
            if(c == -1){
                throw new IllegalArgumentException("Unexpected file end.");
            }
            char ch = (char) c;
            if(ch == '<'){
                throw new IllegalArgumentException("Double open tag.");
            }
            element.append(ch);
        }
        return element.toString();
    }


    private int readChar() throws IOException {
        int c = buffIn.read();
        if(c == -1 && !mainElementIsFound){
            throw new IllegalArgumentException("File is empty.");
        }
        //TODO fixme
        if(c == -1 && !nodePath.isEmpty()){
            throw new IllegalArgumentException("XML closed before end");
        }
//        if()
        return c;
    }

    private boolean checkElementClose(Element element) {
        if(element.getType() != ElementType.CLOSE){
            return false;
        }
        String elementName =
                element.getName().trim();
        if(nodePath.isEmpty() ||
                !elementName.equals(nodePath.getTailNode().getName())){
            //TODO change message and exc
            throw new IllegalArgumentException("The end of an element before close.");
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        buffIn.close();
    }


}
