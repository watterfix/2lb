package fabriki;

import model.kontent.Kontent;
import model.kontent.KontentTip;

/**
 * Фабрика для создания контента слайдов
 * Аналогично специализированным фабрикам из вашего проекта
 */
public interface KontentFabrika {

    /**
     * Создает контент указанного типа
     * @param tip тип контента
     * @param parametri параметры создания
     * @return созданный контент
     */
    Kontent sozdatKontent(KontentTip tip, Object... parametri);

    /**
     * Клонирует существующий контент
     * @param original оригинальный контент
     * @return клон контента
     */
    Kontent klonirovatKontent(Kontent original);
}