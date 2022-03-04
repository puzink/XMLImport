package app.xml;

import lombok.Getter;

@Getter
public class Attribute {

    private String key;
    private String value;

    public Attribute(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Attribute(String str){
        //TODO
    }
}
