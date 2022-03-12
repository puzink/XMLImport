package app.service.converter;

public abstract class AbstractConverter<T> implements StringConverter<T> {
    @Override
    public T convert(String s) {
        if(s == null || s.equals("null")){
            return null;
        }
        return convertNotNullString(s);
    }

    public abstract T convertNotNullString(String s);
}
