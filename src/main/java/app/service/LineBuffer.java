package app.service;

import app.model.Line;
import app.xml.XMLFileParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineBuffer {

    private static final int DEFAULT_BUFFER_SIZE = 10;

    private final XMLFileParser xmlFileParser;
    private List<Line> lines;
    private Iterator<Line> iterator;

    public LineBuffer(XMLFileParser file){
        this.xmlFileParser = file;
        lines = new ArrayList<>(10);
    }

    public Line getLine() {
        if(lines == null
                || lines.isEmpty()
                || !iterator.hasNext()){
            fill();
            iterator = lines.iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

    private void fill(){
        lines = new ArrayList<>(DEFAULT_BUFFER_SIZE);
        //TODO fill buffer
    }

}
