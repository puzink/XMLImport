package app.xml;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class XMLFileParser implements AutoCloseable{

    private final File file;
    private final BufferedReader buffIn;

    private final NodesBranch branch = new NodesBranch();

    public XMLFileParser(File file) throws FileNotFoundException {
        this.file = file;

        FileInputStream fileInput = new FileInputStream(file);
        FileChannel channel =fileInput.getChannel();
        buffIn =new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));
    }

    public Node findNextNode() throws IOException{
        Element element = findNextElement();
        if(checkElementClose(element)){
            branch.removeLast();
            return findNextNode();
        }

        Node newNode = new Node(branch.getTailNode(), element);
        branch.push(newNode);
        return newNode;
    }

    private Element findNextElement() throws IOException {
        readCharsBeforeElement();
        return new Element(readElement());
    }

    private void readCharsBeforeElement() throws IOException {
        int c;
        while((c = readChar()) != '<'){
            char ch = (char) c;
            if(branch.isEmpty() && !String.valueOf(ch).isBlank()){
                throw new IllegalArgumentException("Unexpected symbol occurs.");
            }

            if(String.valueOf(ch).isBlank()
                    && branch.getTailNode().isBodyEmpty()){
                continue;
            }
            branch.appendIntoBody(ch);
        }
    }

    private String readElement() throws IOException {
        StringBuilder element = new StringBuilder();
        int c;
        while((c = readChar()) != '>'){
            if(c == -1){
                throw new IllegalArgumentException("File closed before the end of XML.");
            }
            element.append((char) c);
        }
        return element.toString();
    }


    private int readChar() throws IOException {
        int c = buffIn.read();
        if(c == -1 && !branch.isEmpty()){
            throw new IllegalArgumentException("XML closed before end");
        }
        return c;
    }

    private boolean checkElementClose(Element element) {
        if(element.getName().charAt(0) != '/'){
            return false;
        }
        String elementName =
                element.getName().substring(1)
                        .trim();
        if(branch.isEmpty() ||
                !elementName.equals(branch.getTailNode().getName())){
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
