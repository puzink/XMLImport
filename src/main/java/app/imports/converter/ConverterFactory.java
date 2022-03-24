package app.imports.converter;

import app.table.DataType;

import java.util.EnumMap;
import java.util.Map;

public class ConverterFactory {

    private static final ConverterFactory factory = new ConverterFactory();

    private final Map<DataType, StringConverter<?>> stringConverters;

    {
        stringConverters = new EnumMap<>(DataType.class);
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
