package app.xml;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class XMLFileParser implements AutoCloseable{

    private File file;
    private BufferedReader buffIn;
    private XMLUnit root = null;
    private XMLUnit currentUnit = null;

    public XMLFileParser(File file) throws FileNotFoundException {
        this.file = file;

        FileInputStream fileInput = new FileInputStream(file);
        FileChannel channel =fileInput.getChannel();
        buffIn =new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));
    }

    public XMLUnit findNextNode() throws IOException{
        String elemNameWithAttributes = findNextElement();
        if(checkElementClose(elemNameWithAttributes)){
            XMLUnit prev = currentUnit.getPrev();
            currentUnit.setPrev(null);
            currentUnit = prev;
            return findNextNode();
        }

        if(root == null){
            root = new XMLUnit(null, elemNameWithAttributes.toString());
            currentUnit = root;
        } else {
            currentUnit = new XMLUnit(currentUnit, elemNameWithAttributes.toString());
        }
        return currentUnit;
    }

    public XMLUnit loadUnit(XMLUnit unit){


        return unit;
    }

    public String findNextElement() throws IOException {
        int c;
        while((c = readChar()) != '<'){
            if(currentUnit != null){
                currentUnit.appendIntoBody(c);
            }
        }
        StringBuilder elementAttributes = new StringBuilder();
        while((c = readChar()) != '>'){
            if(c == -1){
                throw new IllegalArgumentException("XML closed before end");
            }
            elementAttributes.append(c);
        }

        return elementAttributes.toString();
    }


    private boolean checkElementClose(String elementAttributes) {
        if(elementAttributes.charAt(0) != '/'){
            return false;
        }
        String elementName =
                elementAttributes.substring(1)
                .trim();
        if(!elementName.equals(currentUnit.getName())){
            //TODO change message and exc
            throw new IllegalArgumentException("Error in element close.");
        }
        return true;
    }

    private int readChar() throws IOException {
        int c = buffIn.read();
        if(c == -1 && root != null){
            throw new IllegalArgumentException("XML closed before end");
        }
        if(root == null && !String.valueOf(c).isBlank()){
            throw new IllegalArgumentException("Unexpected symbol occurs.");
        }
        return c;
    }

    @Override
    public void close() throws IOException {
        buffIn.close();
    }


}
