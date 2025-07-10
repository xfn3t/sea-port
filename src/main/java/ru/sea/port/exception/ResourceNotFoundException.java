package ru.sea.port.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Исключение, выбрасываемое при отсутствии запрошенного ресурса в БД.
 * Формирует понятное сообщение вида "Entity не найден(а) по field = 'value'".
 *
 * @param resourceName имя сущности (например, "User")
 * @param fieldName    имя поля, по которому искали (например, "id")
 * @param fieldValue   значение этого поля
 */
@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    private static final long serialVersionUID = 1L;

    /**
     * Основной конструктор.
     *
     * @param resourceName — имя сущности (например, "User")
     * @param fieldName    — по какому полю искали (например, "id")
     * @param fieldValue   — значение этого поля
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // формируем сообщение, пример: "User не найден по id = '123'"
        super(String.format("%s не найден(а) по %s = '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName    = fieldName;
        this.fieldValue   = fieldValue;
    }

    /**
     * Конструктор с произвольным сообщением.
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName    = null;
        this.fieldValue   = null;
    }
}
