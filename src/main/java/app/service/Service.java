package app.service;

import app.jdbc.DAO;
import app.model.Column;
import app.model.Row;
import app.model.Table;
import app.model.dto.TableDto;
import app.service.converter.ConverterFactory;
import app.xml.Attribute;
import app.xml.Node;
import app.xml.TableReader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Service {

    private static final int DEFAULT_ROW_COUNT = 100;
    private static final String UNIQUE_ATTRIBUTE = "unique";
    private static final String COLUMNS_ATTRIBUTE = "columns";
    private static final String NAME_ATTRIBUTE = "name";

    private final DAO dao;
    private final TableReader tableReader;

    public Service(DAO dao, TableReader tableReader) {
        this.dao = dao;
        this.tableReader = tableReader;
    }

    public long importRows() throws IOException {
        TableDto tableDto = readTable();
        List<Row> rows = readRows(DEFAULT_ROW_COUNT, tableDto);
        long insertedRows = 0;
        while(!rows.isEmpty()){
            convertValues(rows, tableDto.getTable().getColumns());
            rows = removeDuplicates(rows, tableDto.getTable().getName(), tableDto.getUniqueColumns());
            insertedRows += dao.insertRows(rows, tableDto.getTable());

            rows = readRows(DEFAULT_ROW_COUNT, tableDto);
        }


        return insertedRows;
    }

    private void convertValues(List<Row> rows, List<Column> tableColumns) {
        for(Row row : rows){
            for(Column column : tableColumns){
                if(row.containsColumn(column)){
                    String stringValue = (String) row.get(column);
                    Object convertedValue = ConverterFactory.getFactory()
                            .getRightConverter(column.getType())
                            .convert(stringValue);
                    row.addValue(column.getName(), convertedValue);
                }
            }
        }
    }


    private List<Row> readRows(int rowCount, TableDto tableDto) throws IOException {
        Row row;
        List<Row> rows = new ArrayList<>();
        while(rows.size() < rowCount && (row = tableReader.readRow()) != null){
            if(!Objects.equals(row.getValues().size(),tableDto.getColumnsForInsert().size())){
                //TODO logging
                continue;
            }
            if(!row.containsColumns(tableDto.getColumnsForInsert())){
                //TODO logging
                continue;
            }
            rows.add(row);
        }
        return rows;
    }

//    private void compareRowColumnsWithTable(List<Row> rows, Table table) {
//        for(Row row : rows){
//            Map<String, Object> values = row.getValues();
//            Optional<String> notExistedColumn = values.keySet().stream()
//                    .filter((colName) -> !table.containsColumn(colName))
//                    .findAny();
//            if(notExistedColumn.isPresent()){
//                throw new IllegalArgumentException(
//                        String.format("Row has a column '%s' that does not exist in the table(%s). ",
//                                notExistedColumn.get(),
//                                table.getName()
//                        )
//                );
//            }
//        }
//    }

    //TODO rename
    private TableDto readTable() throws IOException {
        Node tableNode = tableReader.getTable();
        checkAttributesCount(tableNode);
        String tableName = tableNode.getTag().getAttributesBy(Attribute.filterByName(NAME_ATTRIBUTE)).get(0).getValue().trim();
        List<Column> tableColumns = dao.getTableColumns(tableName);
        Table table = new Table(tableName, tableColumns);

        List<Column> uniqueColumns =
                splitAttributeValue(tableNode.getTag().getAttributesBy(Attribute.filterByName(UNIQUE_ATTRIBUTE)).get(0));
        List<Column> columnsForInsert =
                splitAttributeValue(tableNode.getTag().getAttributesBy(Attribute.filterByName(COLUMNS_ATTRIBUTE)).get(0));

        TableDto tableDto = new TableDto(table, uniqueColumns, columnsForInsert);
        checkTableColumns(tableDto);
        return tableDto;
    }

    private List<Row> removeDuplicates(List<Row> rows, String tableName, List<Column> uniqueColumns) {
        if(uniqueColumns.isEmpty() || rows.isEmpty()){
            return rows;
        }

        List<Row> result = new ArrayList<>();
        List<Boolean> isRowDuplicate =
                dao.anyDuplicates(rows, tableName, uniqueColumns);
        for(int i = 0; i<rows.size();++i){
            if(!isRowDuplicate.get(i)){
                result.add(rows.get(i));
            }
        }
        //TODO delete duplicate rows in the list
        return result;
    }

    private void checkTableColumns(TableDto tableDto) {

        Optional<Column> uniqueColumn =
                findNotExistedColumn(tableDto.getUniqueColumns(), tableDto.getColumnsForInsert());
        if(uniqueColumn.isPresent()){
            throw new IllegalArgumentException(
                    String.format(
                            "Unique column '%s' is not specified in the '%s' attribute.",
                            uniqueColumn.get().getName(),
                            COLUMNS_ATTRIBUTE
                    )
            );
        }
        Optional<Column> column =
                findNotExistedColumn(tableDto.getColumnsForInsert(), tableDto.getTable().getColumns());
        if(column.isPresent()){
            throw new IllegalArgumentException(
                    String.format(
                            "Column '%s' in the '%s' attribute is not specified in the table.",
                            column.get().getName(),
                            COLUMNS_ATTRIBUTE
                    )
            );
        }

        Optional<Column> duplicateUniqueColumn = findDuplicateColumn(tableDto.getUniqueColumns());
        if(duplicateUniqueColumn.isPresent()){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' has a duplicate value '%s'.",
                            UNIQUE_ATTRIBUTE,
                            duplicateUniqueColumn.get().getName()
                    )
            );
        }

        Optional<Column> duplicateTableColumn = findDuplicateColumn(tableDto.getColumnsForInsert());
        if(duplicateTableColumn.isPresent()){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' has a duplicate value '%s'.",
                            COLUMNS_ATTRIBUTE,
                            duplicateTableColumn.get().getName()
                    )
            );
        }
    }

    private Optional<Column> findDuplicateColumn(List<Column> columns) {
        Set<Column> columnSet = new HashSet<>();
        for(Column col : columns){
            if(columnSet.contains(col)){
                return Optional.of(col);
            }
            columnSet.add(col);
        }
        return Optional.empty();
    }

    private Optional<Column> findNotExistedColumn(List<Column> a, List<Column> target) {
        for(Column colA : a){
            if(!target.contains(colA)){
               return Optional.of(colA);
            }
        }
        return Optional.empty();
    }

    private void checkAttributesCount(Node tableNode) {
        List<Attribute> uniqueAttribute =
                tableNode.getTag().getAttributesBy(Attribute.filterByName(UNIQUE_ATTRIBUTE));
        List<Attribute> columnsAttribute =
                tableNode.getTag().getAttributesBy(Attribute.filterByName(COLUMNS_ATTRIBUTE));
        List<Attribute> tableName =
                tableNode.getTag().getAttributesBy(Attribute.filterByName(NAME_ATTRIBUTE));

        if(tableName.isEmpty()){
            throw new IllegalArgumentException("Table name is not defined.");
        }
        if(tableName.size() > 1){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be unique in the table tag.", NAME_ATTRIBUTE)
            );
        }

        if(uniqueAttribute.size() > 1){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be unique in the table tag.", UNIQUE_ATTRIBUTE)
            );
        }

        if(columnsAttribute.size() > 1){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be unique in the table tag.", COLUMNS_ATTRIBUTE)
            );
        }
        if(columnsAttribute.isEmpty()){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be in the table tag.", COLUMNS_ATTRIBUTE)
            );
        }
    }

    private List<Column> splitAttributeValue(Attribute attribute){
        return Arrays.stream(attribute.getValue().split(";", -1))
                .map(String::trim)
                .map(Column::new)
                .collect(Collectors.toList());
    }


}
