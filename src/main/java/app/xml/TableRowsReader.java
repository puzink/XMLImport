package app.xml;

import app.model.Row;
import app.xml.exception.XmlParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableRowsReader implements TableReader{

    private final XmlParser nodeSupplier;
    private Node rowNode = null;
    private Node tableNode = null;

    private static final String TABLE_TAG_NAME = "table";
    private static final String ROW_TAG_NAME = "row";


    public TableRowsReader(XmlParser nodeSupplier) throws IOException{
        this.nodeSupplier = nodeSupplier;
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
        if(rowNode == null && nodeSupplier.hasNextNode()){
            rowNode = nodeSupplier.getNextNode();
        }
        if(rowNode == null){
            return null;
        }
        if(!rowNode.getTag().getName().equals(ROW_TAG_NAME)){
            throw new IllegalArgumentException("A non-row element was encountered in the table");
        }

        List<Node> nestedRowNodes = new ArrayList<>();
        Node cell = null;
        //FIXME вложенные и дублирующие ноды
        while(rowNode.isOpened()){
            if(!nodeSupplier.hasNextNode()){
                rowNode = null;
                break;
            }
            cell = nodeSupplier.getNextNode();
            if(startNewRow(cell)){
                rowNode = cell;
                return new Row(nestedRowNodes);
            }
            nestedRowNodes.add(cell);
        }

        return new Row(nestedRowNodes);
    }

    private Node readTableElement() throws IOException{
        Node node = nodeSupplier.getNextNode();
        if(!node.getTag().getName().equals(TABLE_TAG_NAME)){
            //TODO change XML
            throw new IllegalArgumentException("XML does not start with table element.");
        }
        return node;
    }

    private boolean startNewRow(Node cell) {
        return cell.getParent() != rowNode && cell.getName().equals(ROW_TAG_NAME);
    }
}
