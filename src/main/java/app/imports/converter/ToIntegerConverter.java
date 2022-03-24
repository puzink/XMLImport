package app.imports.converter;

/**
 * Конвертирует строку в {@link Integer}.
 */
public class ToIntegerConverter extends NullableStringConverter<Integer> {

    /**
     * Конвертирует строку в {@link Integer}.
     * @param s - строка
     * @return число
     * @throws NumberFormatException - если преобразовать не получается
     */
    @Override
    public Integer convertNotNullString(String s) {
        return Integer.valueOf(s.trim());
    }
}
