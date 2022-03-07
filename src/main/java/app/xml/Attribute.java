package app.xml;

import lombok.Getter;

@Getter
public class Attribute {

    private String name;
    private String value;

    public Attribute(String name, String value) {
        validateName(name);

        this.name = name.trim();
        this.value = value;
    }

    private void validateName(String name){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Attribute name must not be empty.");
        }
        if(!Character.isLetter(name.charAt(0))){
            throw new IllegalArgumentException("Attribute name must start with alphabetic character.");
        }
    }

    public String toString(){
        return String.format("%s=\"%s\"", name, value);
    }
}
