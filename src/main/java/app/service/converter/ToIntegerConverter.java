package app.service.converter;

public class ToIntegerConverter extends AbstractConverter<Integer> {

    @Override
    public Integer convertNotNullString(String s) {
        return Integer.valueOf(s);
    }
}
