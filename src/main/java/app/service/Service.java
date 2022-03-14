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

    private static final int DEFAULT_ROW_COUNT = 50;
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
        long readRows = 0;
        while(!rows.isEmpty()){
            readRows += rows.size();
            rows = convertValues(rows, tableDto.getTable().getColumns());
            rows = removeDuplicates(rows, tableDto.getTable().getName(), tableDto.getUniqueColumns());
            insertedRows +=
                    dao.insertRows(rows, tableDto.getColumnsForInsert(), tableDto.getTable().getName());

            System.out.println("Read rows = " + readRows);
            rows = readRows(DEFAULT_ROW_COUNT, tableDto);
        }

        return insertedRows;
    }

    private List<Row> convertValues(List<Row> rows, List<Column> tableColumns) {
        List<Row> result = new ArrayList<>();
        for(Row row : rows){
            try{
                Row convertedRow = convertRowValues(row, tableColumns);
                result.add(convertRowValues(row, tableColumns));
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return result;
    }

    private Row convertRowValues(Row row, List<Column> tableColumns){
        Map<String, Object> convertedValues = new HashMap<>();
        for(Column column : tableColumns){
            if(row.containsColumn(column)){
                String stringValue = (String) row.get(column);
                Object convertedValue = ConverterFactory.getFactory()
                        .getRightConverter(column.getType())
                        .convert(stringValue);
                convertedValues.put(column.getName(), convertedValue);
            }
        }
        return new Row(convertedValues);
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

    //TODO rename
    private TableDto readTable() throws IOException {
        Node tableNode = tableReader.getTable();
        checkAttributesCount(tableNode);
        String tableName = tableNode.getElement().getAttributesBy(Attribute.filterByName(NAME_ATTRIBUTE)).get(0).getValue().trim();
        List<Column> tableColumns = dao.getTableColumns(tableName);
        Table table = new Table(tableName, tableColumns);

        List<Column> uniqueColumns =
                splitAttributeValue(tableNode.getElement().getAttributesBy(Attribute.filterByName(UNIQUE_ATTRIBUTE)).get(0));
        List<Column> columnsForInsert =
                splitAttributeValue(tableNode.getElement().getAttributesBy(Attribute.filterByName(COLUMNS_ATTRIBUTE)).get(0));

        TableDto tableDto = new TableDto(table, uniqueColumns, columnsForInsert);
        checkTableColumns(tableDto);
        return tableDto;
    }

    private List<Row> removeDuplicates(List<Row> rows, String tableName, List<Column> uniqueColumns) {
        if(uniqueColumns.isEmpty() || rows.isEmpty()){
            return rows;
        }

        List<Row> result = new ArrayList<>();
        List<Boolean> isRowDuplicateInTable =
                dao.hasDuplicate(rows, tableName, uniqueColumns);
        Set<Row> rowsToInsert = new HashSet<>();
        for(int i = 0; i<rows.size();++i){
            Row rowProjection = rows.get(i).projectOnto(uniqueColumns);
            if(!isRowDuplicateInTable.get(i)
                    && !rowsToInsert.contains(rowProjection)){
                result.add(rows.get(i));
                rowsToInsert.add(rowProjection);
            }
        }

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
                tableNode.getElement().getAttributesBy(Attribute.filterByName(UNIQUE_ATTRIBUTE));
        List<Attribute> columnsAttribute =
                tableNode.getElement().getAttributesBy(Attribute.filterByName(COLUMNS_ATTRIBUTE));
        List<Attribute> tableName =
                tableNode.getElement().getAttributesBy(Attribute.filterByName(NAME_ATTRIBUTE));

        if(tableName.isEmpty()){
            throw new IllegalArgumentException("Table name is not defined.");
        }
        if(tableName.size() > 1){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be unique in the table element.", NAME_ATTRIBUTE)
            );
        }

        if(uniqueAttribute.size() > 1){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be unique in the table element.", UNIQUE_ATTRIBUTE)
            );
        }

        if(columnsAttribute.size() > 1){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be unique in the table element.", COLUMNS_ATTRIBUTE)
            );
        }
        if(columnsAttribute.isEmpty()){
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be in the table element.", COLUMNS_ATTRIBUTE)
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
