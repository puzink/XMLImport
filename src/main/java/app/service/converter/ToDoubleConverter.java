package app.service.converter;

public class ToDoubleConverter extends AbstractConverter<Double> {
    @Override
    public Double convertNotNullString(String s) {
        return Double.valueOf(s.trim());
    }
}
