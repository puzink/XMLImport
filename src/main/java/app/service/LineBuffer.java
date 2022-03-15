package app.service;

import app.model.Row;
import app.xml.XmlLazyParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineBuffer {

    private static final int DEFAULT_BUFFER_SIZE = 10;

    private final XmlLazyParser xmlFileParser;
    private List<Row> rows;
    private Iterator<Row> iterator;

    public LineBuffer(XmlLazyParser file){
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
