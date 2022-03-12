package app.service.converter;

public class ToStringConverter extends AbstractConverter<String>{
    @Override
    public String convertNotNullString(String s) {
        return s;
    }
}
