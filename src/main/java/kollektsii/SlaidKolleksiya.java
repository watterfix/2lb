package kollektsii;

import model.Slaid.Slaid;
import java.util.Iterator;

/**
 * Интерфейс кастомной коллекции слайдов
 * Вместо обычного массива - гибкая структура
 */
public interface SlaidKolleksiya extends Iterable<Slaid> {

    // Основные операции
    void dobavit(Slaid slaid);
    void dobavit(int index, Slaid slaid);
    void udalit(Slaid slaid);
    void udalit(int index);
    Slaid poluchit(int index);
//    получить слайд по его индексу (номеру). Это основной метод для перехода к конкретному слайду.
    int razmer();
    boolean pusto();
    void ochistit();

    // Специальные операции для слайдов
    void pomenyatMesta(int index1, int index2);
//    поменять два слайда местами.
    void peremestit(int otIndex, int kIndex);
//    переместить слайд с одной позиции на другую.
    void obratniyPoryadok();
//развернуть всю последовательность слайдов.
    // Поиск
    int naytiIndex(Slaid slaid);
    boolean soderzhit(Slaid slaid);

    // Итераторы
    Iterator<Slaid> iterator();
    Iterator<Slaid> iteratorSkonca();
    Iterator<Slaid> iteratorSOpredelennogoMesta(int startIndex);
}