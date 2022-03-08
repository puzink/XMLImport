package app.service;

import app.jdbc.DAO;

public class Service {

    private DAO dao = null;

//    public void importXMLFile(File file) throws IOException {
//        XMLFileParser parser = new XMLFileParser(file);
//        LineBuffer lineBuffer = new LineBuffer(parser);
//
//        Set<Line> lines = new HashSet<>();
//        Line line = null;
//        while((line = lineBuffer.getLine()) != null){
//            lines.add(line);
//            //TODO change 10 and "table"
//            if(lines.size() == 10){
//                dao.insertLines(lines, "table");
//                lines.clear();
//            }
//        }
//
//    }


}
