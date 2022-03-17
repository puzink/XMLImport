package app.service.converter;

/**
 * Конвертирует строку в {@link Long}.
 */
public class ToLongConverter extends NullableStringConverter<Long> {

    /**
     * Конвертирует строку в {@link Long}.
     * @param s - строка
     * @return число
     * @throws NumberFormatException - если преобразовать не получается
     */
    @Override
    public Long convertNotNullString(String s) {
        return Long.valueOf(s.trim());
    }
}
