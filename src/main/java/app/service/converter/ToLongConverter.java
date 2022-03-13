package app.service.converter;

public class ToLongConverter extends AbstractConverter<Long>{
    @Override
    public Long convertNotNullString(String s) {
        return Long.valueOf(s.trim());
    }
}
