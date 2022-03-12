package app.xml;

import app.xml.exception.XmlExpectedEndOfFileException;
import app.xml.exception.XmlNoMoreNodesException;
import app.xml.exception.XmlParseException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class XmlParserImpl implements XmlParser, AutoCloseable{

    private final File file;
    private final BufferedReader buffIn;
    private XmlTagParser tagParser;

    private final NodePath nodePath = new NodePath();
    private boolean rootTagIsFound = false;
    private Node nextNode = null;
    //TODO add cursor
    private CursorPosition cursor = new CursorPosition();

    private IOException thrownException = null;

    public XmlParserImpl(File file, XmlTagParser tagParser) throws IOException{
        this.file = file;
        this.tagParser = tagParser;

        FileInputStream fileInput = new FileInputStream(file);
        FileChannel channel =fileInput.getChannel();
        buffIn = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));

        nextNode = findNextNode();
        rootTagIsFound = true;
    }

    @Override
    public Node getNextNode() throws IOException{
        if(thrownException != null){
            throw thrownException;
        }
        if(!hasNextNode()){
            thrownException = new XmlNoMoreNodesException("There is no more nodes in the file.", cursor);
            throw thrownException;
        }

        Node currentNode = nextNode;
        try{
            nextNode = findNextNode();
        } catch (XmlExpectedEndOfFileException e){
            nextNode = null;
        }
        return currentNode;

    }

    @Override
    public boolean hasNextNode() throws IOException {
        if(thrownException instanceof XmlNoMoreNodesException
                || thrownException instanceof XmlExpectedEndOfFileException){
            return false;
        }
        if(thrownException != null){
            throw thrownException;
        }
        return nextNode != null;
    }

    private Node findNextNode() throws IOException{
        Tag tag = getNextTag();
        if(rootTagIsFound && nodePath.isEmpty()){
            thrownException = new XmlParseException("Multiply root tags.", cursor);
            throw thrownException;
        }
        if(checkTagClose(tag)){
            nodePath.getTailNode().setStatus(NodeStatus.CLOSED);
            nodePath.removeLast();
            return findNextNode();
        }

        Node newNode = new Node(nodePath.getTailNode(), tag, NodeStatus.OPENED);
        nodePath.add(newNode);
        return newNode;
    }

    private Tag getNextTag() throws IOException {
        readCharsBeforeNextTag();
        String tag = readTag();
        return tagParser.parseTag(tag);
    }

    private void readCharsBeforeNextTag() throws IOException {
        int c;
        while((c = readChar()) != '<'){
            char ch = (char) c;

            if(nodePath.isEmpty() && rootTagIsFound && c == -1){
                //TODO add cursor
                throw new XmlExpectedEndOfFileException(null);
            }

            if(nodePath.isEmpty() && !Character.isWhitespace(ch)){
                thrownException = new XmlParseException("Unexpected symbol occurs.", cursor);
                throw thrownException;
            }

            if(!nodePath.isEmpty()
                    && (!nodePath.getTailNode().isBodyEmpty()
                    || !Character.isWhitespace(ch))){
                nodePath.appendIntoBody(ch);
            }
        }
    }

    private String readTag() throws IOException {
        StringBuilder element = new StringBuilder();
        int c;
        while((c = readChar()) != '>'){
            if(c == -1){
                thrownException = new XmlParseException("Unexpected file end.", cursor);
                throw thrownException;
            }
            char ch = (char) c;
            if(ch == '<'){
                thrownException = new XmlParseException("Double open tag.", cursor);
                throw thrownException;
            }
            element.append(ch);
        }
        return element.toString();
    }


    private int readChar() throws IOException {
        int c = buffIn.read();
        if(c == -1 && !rootTagIsFound){
            thrownException = new XmlParseException("File is empty.", cursor);
            throw thrownException;
        }
        if(c == -1 && !nodePath.isEmpty()){
            thrownException = new XmlParseException("Xml file closed before end.", cursor);
            throw thrownException;
        }

        return c;
    }

    private boolean checkTagClose(Tag tag) throws IOException {
        if(tag.getType() != TagType.CLOSE){
            return false;
        }
        String tagName = tag.getName().trim();
        if(nodePath.isEmpty() ||
                !tagName.equals(nodePath.getTailNode().getName())){
            thrownException = new XmlParseException("Close tag before the open one.", cursor);
            throw thrownException;
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        buffIn.close();
    }


}
