package app.xml;

import app.model.Row;
import app.xml.exception.XmlParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLTableReader implements TableReader{

    private XMLFileParser xmlParser;
    private Node rowNode = null;
    private Node tableNode = null;

    private static final String TABLE_TAG_NAME = "table";
    private static final String ROW_TAG_NAME = "row";


    public XMLTableReader(XMLFileParser xmlParser) throws IOException, XmlParseException {
        this.xmlParser = xmlParser;
    }


    @Override
    public Node getTable() throws IOException {
        if(tableNode == null){
            tableNode = readTableElement();
        }
        return tableNode;
    }

    @Override
    /*
    *   Возвращает null, если больше нет строк в таблице.
    *
     */
    public Row readRow() throws IOException{
        if(tableNode == null){
            tableNode = readTableElement();
        }

        if(rowNode == null && xmlParser.hasNextNode()){
            rowNode = xmlParser.getNextNode();
        }
        if(rowNode == null){
            return null;
        }
        if(!rowNode.getTag().getName().equals(ROW_TAG_NAME)){
            throw new IllegalArgumentException("A non-row element was encountered in the table");
        }

        Map<String, String> RowValues = new HashMap<>();
        Node cell = null;
        while(rowNode.isOpened()){
            cell = xmlParser.getNextNode();
            if(startNewLine(cell)){
                rowNode = cell;
                return new Row(RowValues);
            }
            //TODO вложенный node
            if(isNestedLine(cell)){
                closeLineAndFindNext();
                readRow();
            }
        }

        //TODO


        return null;
    }

    private Node readTableElement() throws IOException, XmlParseException {
        Node node = xmlParser.getNextNode();
        if(!node.getTag().getName().equals(TABLE_TAG_NAME)){
            //TODO change exception
            throw new IllegalArgumentException("XML does not start with table element.");
        }
        return node;
    }

    private void closeLineAndFindNext() throws IOException, XmlParseException {
        Node node = xmlParser.getNextNode();
//        while(node != null && node.getParent())
    }

    private boolean isNestedLine(Node cell) {
        return cell.getParent() == rowNode
                && cell.getTag().getName().equals("line");
    }

    private boolean startNewLine(Node cell) {
        return cell.getParent() != rowNode && cell.getName().equals(ROW_TAG_NAME);
    }
}
