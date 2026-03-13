package fabriki;

import model.Slaid.Slaid;
import model.kontent.Kontent;
import model.animatsiya.Animatsiya;
import java.io.File;


public interface SlaidFabrika {

    /**
     * Создает слайд из файла изображения
     * @param failIzobrazheniya файл с изображением
     * @return созданный слайд
     */
    Slaid sozdatSlaid(File failIzobrazheniya);

    /**
     * Создает текстовый контент
     * @param tekst текст для отображения
     * @param ugol угол размещения текста
     * @param masshtab масштаб текста (0.5-3.0)
     * @return текстовый контент
     */
    Kontent sozdatTekstKontent(String tekst, model.kontent.UgolSlaida ugol, double masshtab);

    /**
     * Создает контент со смайликом
     * @param tipSmailika тип смайлика
     * @param x координата X
     * @param y координата Y
     * @return контент со смайликом
     */
    Kontent sozdatSmailikKontent(model.kontent.SmailikKontent.TipSmailika tipSmailika, int x, int y);

    /**
     * Создает анимацию для слайда
     * @param tip тип анимации
     * @param prodolzhitelnost продолжительность в мс
     * @return объект анимации
     */
    Animatsiya sozdatAnimatsiyu(model.animatsiya.TipAnimatsii tip, int prodolzhitelnost);

    /**
     * Создает заметку для слайда
     * @param tekst текст заметки
     * @return текст заметки
     */
    String sozdatZametku(String tekst);
}