package app.service.converter;

public class ToFloatConverter extends AbstractConverter<Float>{
    @Override
    public Float convertNotNullString(String s) {
        return Float.valueOf(s.trim());
    }
}
