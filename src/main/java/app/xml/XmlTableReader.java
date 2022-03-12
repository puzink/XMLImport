package app.xml;

import app.model.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlTableReader implements TableReader{

    private final XmlParser xmlParser;
    private Node prevRowNode = null;
    private Node currentRowNode = null;

    private Node tableNode = null;

    private static final String TABLE_TAG_NAME = "table";
    private static final String ROW_TAG_NAME = "row";


    public XmlTableReader(XmlParser xmlParser){
        this.xmlParser = xmlParser;
    }

    @Override
    public Node getTable() throws IOException {
        if(tableNode == null){
            tableNode = readTableElement();
        }
        return tableNode;
    }

    /*
     *   Возвращает null, если больше нет строк в таблице.
     *
     */
    @Override
    public Row readRow() throws IOException{
        if(tableNode == null){
            tableNode = readTableElement();
        }
        if(currentRowNode == null || prevRowNode == currentRowNode){
            currentRowNode = xmlParser.hasNextNode()
                    ? xmlParser.getNextNode()
                    : null;
        }
        if(currentRowNode == null){
            return null;
        }
        if(!currentRowNode.getTag().getName().equals(ROW_TAG_NAME)){
            //TODO change exception
            throw new IllegalArgumentException("A non-row element was encountered in the table");
        }

        if(currentRowNode.isClosed()){
            prevRowNode = currentRowNode;
            return new Row(new ArrayList<>());
        }

        List<Node> nestedNodesInRow = new ArrayList<>();
        Node cell = null;
        //FIXME вложенные и дублирующие ноды
        while(currentRowNode.isOpened()){
            if(!xmlParser.hasNextNode()){
                currentRowNode = null;
                break;
            }
            cell = xmlParser.getNextNode();
//            if(startNewRow(cell)){
//                prevRowNode = currentRowNode;
//                currentRowNode = cell;
//                return new Row(nestedNodesInRow);
//            }
            nestedNodesInRow.add(cell);
        }

        prevRowNode = currentRowNode;

        return new Row(nestedNodesInRow);
    }

    private Node readTableElement() throws IOException{
        Node node = xmlParser.getNextNode();
        if(!node.getTag().getName().equals(TABLE_TAG_NAME)){
            //TODO change XML
            throw new IllegalArgumentException("XML does not start with table element.");
        }
        return node;
    }

//    private boolean startNewRow(Node cell) {
//        return cell.getParent() != currentRowNode && cell.getName().equals(ROW_TAG_NAME);
//    }
}
