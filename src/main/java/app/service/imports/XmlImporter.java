package app.service.imports;

import app.repository.RowRepository;
import app.repository.TableRepository;
import app.table.Column;
import app.table.Row;
import app.table.Table;
import app.service.converter.ConverterFactory;
import app.xml.Attribute;
import app.xml.Node;
import app.xml.XmlTableReader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Считывает строки таблицы из xml-файла и вставляет их.
 */
public class XmlImporter {

    private static final int DEFAULT_ROW_COUNT = 50;
    private static final String UNIQUE_ATTRIBUTE = "unique";
    private static final String COLUMNS_ATTRIBUTE = "columns";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String SEPARATOR = ";";

    private final RowRepository repository;
    private final TableRepository tableRepository;

    public XmlImporter(RowRepository rowRepository,
                       TableRepository tableRepository) {
        this.repository = rowRepository;
        this.tableRepository = tableRepository;
    }


    /**
     * Считывает строки и вставляет те из них, для которых нет дублирующих строк в таблице.
     * Дубли строк определяются с помощью набора столбцов,
     *      который должен быть задан в корневом узле({@link Node}).
     * Сравнение строк происходит через равенство и через проверку на null:
     *      строки {null} и {null} считаются равными.
     * @return количество вставленных строк.
     * @throws IOException - если произошла ошибка во время чтения строк.
     */
    public long importUniqueTableRows(XmlTableReader tableReader)
            throws IOException, XmlImportException {
        ImportTableDto importTableDto = readTableInfo(tableReader);
        List<Row> rows = readRows(DEFAULT_ROW_COUNT, importTableDto, tableReader);
        long insertedRows = 0;
        long readRows = 0;
        while(!rows.isEmpty()){
            readRows += rows.size();

            rows = convertValues(rows, importTableDto.getTable().getColumns());

            insertedRows += repository.insertUniqueRows(
                    rows,
                    importTableDto.getColumnsForInsert(),
                    importTableDto.getUniqueColumns(),
                    importTableDto.getTable().getName()
            );

            System.out.println("Read rows = " + readRows);
            rows = readRows(DEFAULT_ROW_COUNT, importTableDto, tableReader);
        }

        return insertedRows;
    }

    /**
     * Считывает табличный узел и определяет уникальные столбцы
     *          и столбцы, по которым будут вставленны строки.
     * Также производит проверки на корректность столбцов:
     *      1) указанные столбцы должны быть в таблице,
     *      2) уникальные столбцы должны содержаться в столбцах для вставки.
     * @param tableReader - поставщик данных о таблице
     * @return данные о таблице и столбцов, необходимых для проверки уникальности и вставки строк
     * @throws IOException - если произошла ошибка при чтении табличного узла в xml
     */
    private ImportTableDto readTableInfo(XmlTableReader tableReader)
            throws IOException, XmlImportException {
        Node tableNode = tableReader.getTable();
        checkAttributesExistence(tableNode);
        String tableName = tableNode.getElement()
                .getAttributeBy(Attribute.filterByName(NAME_ATTRIBUTE))
                .get().getValue().trim();
        List<Column> tableColumns = tableRepository.getTableColumns(tableName);
        Table table = new Table(tableName, tableColumns);

        List<Column> uniqueColumns =
                splitAttributeValue(
                        tableNode.getElement()
                                .getAttributeBy(Attribute.filterByName(UNIQUE_ATTRIBUTE))
                                .get(),
                        SEPARATOR
                );
        List<Column> columnsForInsert =
                splitAttributeValue(
                        tableNode.getElement()
                                .getAttributeBy(Attribute.filterByName(COLUMNS_ATTRIBUTE))
                                .get(),
                        SEPARATOR
                );

        ImportTableDto importTableDto = new ImportTableDto(table, uniqueColumns, columnsForInsert);
        checkTableColumns(importTableDto);
        return importTableDto;
    }

    /**
     * Преобразует строковые({@link String}) значения строк в типы,
     *      которые определяются из sql-типа столбца.
     * При конвертации создаётся новая строка с измененными значениями, а не изменяется старая.
     * @param rows - строки, значения которых необходимо преобразовать
     * @param tableColumns - столбцы таблицы, для которых нужно преобразовать значения строк
     * @return новые строки с преобразованными значениями
     */
    private List<Row> convertValues(List<Row> rows, List<Column> tableColumns) {
        List<Row> result = new ArrayList<>();
        for(Row row : rows){
            try{
                Row convertedRow = convertRowValues(row, tableColumns);
                result.add(convertedRow);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return result;
    }

    /**
     *
     * Преобразует строковые({@link String}) значения строки для выбранных столбцов.
     * Тип, в который преобразуется значение, определяется из sql-типа столбца.
     * При конвертации создаётся новая строка, а не изменяется старая.
     * @param row - строка
     * @param tableColumns - столбцы, для которых необходимо преобразовать значения
     * @return новая строка с преобразованными значениями
     * @see app.service.converter.StringConverter
     * @see app.table.DataType
     */
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

    /**
     * Считывает указанное количество корректных строк.
     * Строка считается корректной, если кол-во её столбцы совпадают с указанными в xml-файле.
     * Если нужно считать больше строк, чем возможно, тогда считываются все оставшиеся строки.
     * @param rowCount - кол-во строк для чтения
     * @param importTableDto - данные о таблице и заданных в xml-файле столбцах
     * @param tableReader - считыватель строк
     * @return список считанных и корректных строк
     * @throws IOException - если произошла ошибка при чтении строк
     */
    private List<Row> readRows(int rowCount, ImportTableDto importTableDto, XmlTableReader tableReader)
            throws IOException {
        Row row;
        List<Row> rows = new ArrayList<>();
        while(rows.size() < rowCount && (row = tableReader.readRow()) != null){
            if(!Objects.equals(row.getValues().size(), importTableDto.getColumnsForInsert().size())){
                //TODO logging
                continue;
            }
            if(!row.containsColumns(importTableDto.getColumnsForInsert())){
                //TODO logging
                continue;
            }
            rows.add(row);
        }
        return rows;
    }

    /**
     * Проверяет корректность заданных атрибутов:
     *         {@link XmlImporter#UNIQUE_ATTRIBUTE}, {@link XmlImporter#COLUMNS_ATTRIBUTE}.
     * Уникальные столбцы должны быть вложены в заданные для строк,
     *          а вторые должны полностью включаться в столбцы самой таблицы.
     * Также значения атрибутов не должны содержать дублирующие столбцы.
     * @param importTableDto - данные о таблице и заданных в xml-файле столбцах
     */
    private void checkTableColumns(ImportTableDto importTableDto) throws XmlImportException {

        Optional<Column> uniqueColumn =
                findNotExistedColumn(importTableDto.getUniqueColumns(), importTableDto.getColumnsForInsert());
        if(uniqueColumn.isPresent()){
            throw new XmlImportException(
                    String.format(
                            "Unique column '%s' is not specified in the '%s' attribute.",
                            uniqueColumn.get().getName(),
                            COLUMNS_ATTRIBUTE
                    )
            );
        }
        Optional<Column> column =
                findNotExistedColumn(importTableDto.getColumnsForInsert(), importTableDto.getTable().getColumns());
        if(column.isPresent()){
            throw new XmlImportException(
                    String.format(
                            "Column '%s' in the '%s' attribute is not specified in the table.",
                            column.get().getName(),
                            COLUMNS_ATTRIBUTE
                    )
            );
        }

        Optional<Column> duplicateUniqueColumn = findDuplicateColumn(importTableDto.getUniqueColumns());
        if(duplicateUniqueColumn.isPresent()){
            throw new XmlImportException(
                    String.format("Attribute '%s' has a duplicate value '%s'.",
                            UNIQUE_ATTRIBUTE,
                            duplicateUniqueColumn.get().getName()
                    )
            );
        }

        Optional<Column> duplicateTableColumn = findDuplicateColumn(importTableDto.getColumnsForInsert());
        if(duplicateTableColumn.isPresent()){
            throw new XmlImportException(
                    String.format("Attribute '%s' has a duplicate value '%s'.",
                            COLUMNS_ATTRIBUTE,
                            duplicateTableColumn.get().getName()
                    )
            );
        }
    }

    /**
     * Проверяет на наличие столбцов с одиннаковым именем.
     * @param columns - столбцы
     * @return столбец, который встречается более 1 раза. Иначе {@link Optional#empty()}
     */
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

    /**
     * Проверяет вложенность первого списка столбцов во второй.
     * @param first - список столбцов, который должен полностью принадлежать второму
     * @param second - список столбцов, который содержит столбцы первого
     * @return столбец, которого нет во втором списке. Иначе {@link Optional#empty()}
     */
    private Optional<Column> findNotExistedColumn(List<Column> first, List<Column> second) {
        for(Column colF : first){
            if(!second.contains(colF)){
               return Optional.of(colF);
            }
        }
        return Optional.empty();
    }

    /**
     * Проверяет наличие имени таблицы и столбцов, по которым будут вставляться строки.
     * @param tableNode - узел таблицы с атрибутами
     */
    private void checkAttributesExistence(Node tableNode) throws XmlImportException{
        List<Attribute> columnsAttribute =
                tableNode.getElement().getAttributesBy(Attribute.filterByName(COLUMNS_ATTRIBUTE));
        List<Attribute> tableName =
                tableNode.getElement().getAttributesBy(Attribute.filterByName(NAME_ATTRIBUTE));

        if(tableName.isEmpty()){
            throw new XmlImportException("Table name is not defined.");
        }

        if(columnsAttribute.isEmpty()){
            throw new XmlImportException("Column for insert is not defined.");
        }
    }

    /**
     * Создаёт столбцы по именам, заданных в значении атрибута и разделенных сепаратором.
     * @param attribute - атрибут с именами столбцов
     * @param separator - разделелитель между именами столбцов
     * @return набор столбцов, созданных по именам
     */
    private List<Column> splitAttributeValue(Attribute attribute, String separator){
        return Arrays.stream(attribute.getValue().split(separator, -1))
                .map(String::trim)
                .map(Column::new)
                .collect(Collectors.toList());
    }


}
