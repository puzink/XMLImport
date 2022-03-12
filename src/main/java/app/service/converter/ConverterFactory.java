package app.service.converter;

import app.model.DataType;

import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {

    private static final ConverterFactory factory = new ConverterFactory();

    private final Map<DataType, StringConverter<?>> stringConverters;

    {
        //TODO может ли enum поменять свой хэш?
        stringConverters = new HashMap<>();
        stringConverters.put(DataType.STRING, new ToStringConverter());
        stringConverters.put(DataType.INTEGER, new ToIntegerConverter());
        stringConverters.put(DataType.LONG, new ToLongConverter());
        stringConverters.put(DataType.FLOAT, new ToFloatConverter());
        stringConverters.put(DataType.DOUBLE, new ToDoubleConverter());
        stringConverters.put(DataType.BOOLEAN, new ToBooleanConverter());
    }

    public static ConverterFactory getFactory(){
        return factory;
    }

    public StringConverter<?> getRightConverter(DataType toType){
        return stringConverters.get(toType);
    }
}
