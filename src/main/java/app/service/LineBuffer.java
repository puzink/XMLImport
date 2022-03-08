package app.service;

import app.model.Row;
import app.xml.XMLFileParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineBuffer {

    private static final int DEFAULT_BUFFER_SIZE = 10;

    private final XMLFileParser xmlFileParser;
    private List<Row> rows;
    private Iterator<Row> iterator;

    public LineBuffer(XMLFileParser file){
        this.xmlFileParser = file;
        rows = new ArrayList<>(10);
    }

    public Row getLine() {
        if(rows == null
                || rows.isEmpty()
                || !iterator.hasNext()){
            fill();
            iterator = rows.iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

    private void fill(){
        rows = new ArrayList<>(DEFAULT_BUFFER_SIZE);
        //TODO fill buffer
    }

}
