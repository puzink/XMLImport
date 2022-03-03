package app.xml;

import lombok.Data;

import java.util.List;

@Data
public class XMLUnit {
    private String name;
    private List<Property> properties;
    private StringBuilder body;
    private XMLUnit prev;

    public XMLUnit(XMLUnit prev, String innerStr) {
        this.prev = prev;
//        this.name = name;
        int i = parseName(innerStr);
        parseProperties(innerStr);
    }

    public void appendIntoBody(int c){
        body.append(c);
    }

    private int parseName(String innerStr) {
        //TODO
        return 0;
    }

    //TODO
    private void parseProperties(String innerStr) {
        int start = -1;
        for(int i = 0;i<innerStr.length();++i){
          if(innerStr.substring(i,i+1).isBlank()){
             continue;
          }
          if(start == -1){
              start = i;
          }
        }
    }


}
