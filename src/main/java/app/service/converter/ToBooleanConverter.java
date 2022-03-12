package app.service.converter;

public class ToBooleanConverter extends AbstractConverter<Boolean>{
    @Override
    public Boolean convertNotNullString(String s) {
        if(s.trim().equalsIgnoreCase("true")){
            return true;
        }
        if(s.trim().equalsIgnoreCase("false")){
            return true;
        }
        throw new ClassCastException(
                String.format("Cannot convert string '%s' to the boolean type.", s)
        );
    }
}
