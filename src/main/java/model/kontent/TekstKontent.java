package model.kontent;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * Текстовый контент для слайда
 */
public class TekstKontent implements Kontent {
    private String tekst;
    private UgolSlaida ugol;
    private double masshtab; // от 0.5 до 3.0
    private Color tsvet;
    private Font shrift;
    private Point pozitsiya; // вычисляемая позиция

    public TekstKontent(String tekst, UgolSlaida ugol, double masshtab) {
        this.tekst = tekst;
        this.ugol = ugol;
        this.masshtab = masshtab;
        this.tsvet = Color.BLACK;
        this.shrift = new Font("Arial", Font.PLAIN, 24);
    }

    @Override
    public void risovat(Graphics2D grafika, Rectangle granitsiIzobrazheniya) {
        // Сохраняем оригинальные настройки
        Color originalTsvet = grafika.getColor();
        Font originalShrift = grafika.getFont();

        // Применяем масштаб
        Font masshtabirovanniyShrift = shrift.deriveFont(
                (float)(shrift.getSize() * masshtab)
        );
        grafika.setFont(masshtabirovanniyShrift);
        grafika.setColor(tsvet);

        // Вычисляем позицию текста
        vichislitPozitsiyu(granitsiIzobrazheniya);

        // Рисуем текст
        grafika.drawString(tekst, pozitsiya.x, pozitsiya.y);

        // Восстанавливаем настройки
        grafika.setColor(originalTsvet);
        grafika.setFont(originalShrift);
    }

    private void vichislitPozitsiyu(Rectangle granitsi) {
        int otstup = 20; // отступ от края
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D tekstGranitsi = shrift.getStringBounds(tekst, frc);

        int shirinaTeksta = (int) tekstGranitsi.getWidth();
        int visotaTeksta = (int) tekstGranitsi.getHeight();

        switch (ugol) {
            case VERH_LEVO:
                pozitsiya = new Point(
                        granitsi.x + otstup,
                        granitsi.y + otstup + visotaTeksta
                );
                break;
            case VERH_PRAVO:
                pozitsiya = new Point(
                        granitsi.x + granitsi.width - shirinaTeksta - otstup,
                        granitsi.y + otstup + visotaTeksta
                );
                break;
            case NIZ_LEVO:
                pozitsiya = new Point(
                        granitsi.x + otstup,
                        granitsi.y + granitsi.height - otstup
                );
                break;
            case NIZ_PRAVO:
                pozitsiya = new Point(
                        granitsi.x + granitsi.width - shirinaTeksta - otstup,
                        granitsi.y + granitsi.height - otstup
                );
                break;
        }
    }

    @Override
    public void obnovitPozitsiyu(int x, int y) {
        this.pozitsiya = new Point(x, y);
    }

    @Override
    public KontentTip poluchitTip() {
        return KontentTip.TEKST;
    }

    // Геттеры и сеттеры
    public String poluchitTekst() { return tekst; }
    public void ustanovitTekst(String tekst) { this.tekst = tekst; }

    public UgolSlaida poluchitUgol() { return ugol; }
    public void ustanovitUgol(UgolSlaida ugol) { this.ugol = ugol; }

    public double poluchitMasshtab() { return masshtab; }
    public void ustanovitMasshtab(double masshtab) {
        this.masshtab = Math.max(0.5, Math.min(3.0, masshtab));
    }
}