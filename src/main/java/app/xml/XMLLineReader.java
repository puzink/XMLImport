package app.xml;

import app.model.Line;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class XMLLineReader {

    private XMLFileParser xmlParser;
    private Node lineNode = null;
    private Node tableNode = null;

    public XMLLineReader(XMLFileParser xmlParser) throws IOException {
        this.xmlParser = xmlParser;
        readTableElement();
    }

    public Line readLine() throws IOException {
        if(tableNode == null){
            readTableElement();
        }
        if(lineNode == null && xmlParser.hasNextNode()){
            lineNode = xmlParser.getNextNode();
        }
        if(lineNode == null){
            return null;
        }
        if(!lineNode.getElement().getName().equals("line")){
            throw new IllegalArgumentException("A non-line element was encountered in the table");
        }

        Map<String, String> lineValues = new HashMap<>();
        Node cell = null;
        while(lineNode.isOpened()){
            cell = xmlParser.getNextNode();
            if(startNewLine(cell)){
                lineNode = cell;
                return new Line(lineValues);
            }
            //TODO вложенный node
            if(isNestedLine(cell)){
                closeLineAndFindNext();
                readLine();
            }
        }

        //TODO


        return null;
    }

    private void readTableElement() throws IOException {
        if(!xmlParser.hasNextNode()){
            throw new IllegalArgumentException("File is empty");
        }
        Node node = xmlParser.getNextNode();
        if(!node.getElement().getName().equals("table")){
            throw new IllegalArgumentException("XML does not start with table element.");
        }
        tableNode = node;
    }

    private void closeLineAndFindNext() throws IOException {
        Node node = xmlParser.getNextNode();
//        while(node != null && node.getParent())
    }

    private boolean isNestedLine(Node cell) {
        return cell.getParent() == lineNode
                && cell.getElement().getName().equals("line");
    }

    private boolean startNewLine(Node cell) {
        return cell.getParent() != lineNode && cell.getName().equals("line");
    }
}
