package app.model;

import java.util.Arrays;
import java.util.Optional;

public enum DataType {
    INTEGER("integer"),
    DOUBLE("double precision"),
    FLOAT("real"),
    LONG("bigint"),
    STRING("character varying"),
    BOOLEAN("boolean");

    private final String sqlType;

    private DataType(String sqlType){
        this.sqlType = sqlType;
    }

    public static Optional<DataType> getBySqlType(String type){
        return Arrays.stream(DataType.values())
                .filter(dataType -> dataType.sqlType.equals(type))
                .findFirst();
    }

}
