package app.xml;

import app.xml.exception.XmlParseException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class XMLFileParser implements AutoCloseable{

    private final File file;
    private final BufferedReader buffIn;
    private XmlTagParser tagParser;

    private final NodePath nodePath = new NodePath();
    private boolean rootTagIsFound = false;
    private Node nextNode = null;

    public XMLFileParser(File file, XmlTagParser tagParser) throws IOException{
        this.file = file;
        this.tagParser = tagParser;

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

    private Node findNextNode() throws IOException{
        Tag tag = getNextElement();
        if(rootTagIsFound && nextNode == null){
            throw new XmlParseException("Multiply root tags.");
        }
        if(checkElementClose(tag)){
            nodePath.getTailNode().setStatus(NodeStatus.CLOSED);
            nodePath.removeLast();
            return findNextNode();
        }

        Node newNode = new Node(nodePath.getTailNode(), tag, NodeStatus.OPENED);
        nodePath.add(newNode);
        return newNode;
    }

    private Tag getNextElement() throws IOException {
        readCharsBeforeNextElement();
        String elem = readElement();
        return tagParser.parseTag(elem);
    }

    private void readCharsBeforeNextElement() throws IOException {
        int c;
        while((c = readChar()) != '<'){
            char ch = (char) c;
            if(nodePath.isEmpty() && !Character.isWhitespace(ch)){
                throw new XmlParseException("Unexpected symbol occurs.");
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
                throw new XmlParseException("Unexpected file end.");
            }
            char ch = (char) c;
            if(ch == '<'){
                throw new XmlParseException("Double open tag.");
            }
            element.append(ch);
        }
        return element.toString();
    }


    private int readChar() throws IOException {
        int c = buffIn.read();
        if(c == -1 && !rootTagIsFound){
            throw new XmlParseException("File is empty.");
        }
        //TODO fixme
        if(c == -1 && !nodePath.isEmpty()){
            throw new XmlParseException("Xml file closed before end.");
        }
        return c;
    }

    private boolean checkElementClose(Tag tag) throws XmlParseException {
        if(tag.getType() != TagType.CLOSE){
            return false;
        }
        String tagName = tag.getName().trim();
        if(nodePath.isEmpty() ||
                !tagName.equals(nodePath.getTailNode().getName())){
            //TODO change message and exc
            throw new XmlParseException("Close tag before the open one.");
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        buffIn.close();
    }


}
