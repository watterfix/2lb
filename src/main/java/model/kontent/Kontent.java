package model.kontent;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Интерфейс для всех типов контента (текст, смайлики)
 */
public interface Kontent {
    void risovat(Graphics2D grafika, Rectangle granitsiIzobrazheniya);
    void obnovitPozitsiyu(int x, int y);
    KontentTip poluchitTip();

    enum KontentTip {
        TEKST,
        SMAILIK
    }
}