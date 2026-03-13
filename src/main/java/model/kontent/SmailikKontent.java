package model.kontent;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Графический контент - смайлики
 */
public class SmailikKontent implements Kontent {
    private TipSmailika tip;
    private Point pozitsiya;
    private BufferedImage izobrazhenieSmailika;
    private static final int RAZMER = 50;

    public enum TipSmailika {
        VESELIY,
        GRUSTNIY,
        UDIVLENNIY,
        SERDITIY,
        PODMIGIVAYUSHIY
    }

    public SmailikKontent(TipSmailika tip, Point pozitsiya) {
        this.tip = tip;
        this.pozitsiya = pozitsiya;
        zagruzitIzobrazhenie();
    }

    private void zagruzitIzobrazhenie() {
        try {
            // В реальном проекте здесь будет загрузка из resources
            String putKFailu = "resursi/smailiki/" + tip.name().toLowerCase() + ".png";
            izobrazhenieSmailika = ImageIO.read(new File(putKFailu));
        } catch (IOException e) {
            // Если файл не найден, создаем простой смайлик
            sozdatProstoySmailik();
        }
    }

    private void sozdatProstoySmailik() {
        izobrazhenieSmailika = new BufferedImage(RAZMER, RAZMER, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = izobrazhenieSmailika.createGraphics();

        // Рисуем простой смайлик
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(0, 0, RAZMER, RAZMER);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(0, 0, RAZMER, RAZMER);

        // Глаза
        g2d.fillOval(10, 15, 8, 8);
        g2d.fillOval(32, 15, 8, 8);

        // Рот в зависимости от типа
        switch (tip) {
            case VESELIY:
                g2d.drawArc(10, 20, 30, 20, 0, -180);
                break;
            case GRUSTNIY:
                g2d.drawArc(10, 30, 30, 20, 0, 180);
                break;
            case UDIVLENNIY:
                g2d.drawOval(20, 25, 10, 15);
                break;
            case SERDITIY:
                g2d.drawLine(10, 25, 40, 25);
                g2d.drawLine(8, 20, 12, 25); // брови
                g2d.drawLine(38, 20, 42, 25);
                break;
            case PODMIGIVAYUSHIY:
                g2d.fillOval(10, 15, 8, 8);
                g2d.drawArc(32, 15, 8, 8, 0, 180); // подмигивающий глаз
                g2d.drawArc(10, 20, 30, 20, 0, -180);
                break;
        }

        g2d.dispose();
    }

    @Override
    public void risovat(Graphics2D grafika, Rectangle granitsiIzobrazheniya) {
        if (izobrazhenieSmailika != null) {
            grafika.drawImage(izobrazhenieSmailika,
                    pozitsiya.x, pozitsiya.y, RAZMER, RAZMER, null);
        }
    }

    @Override
    public void obnovitPozitsiyu(int x, int y) {
        this.pozitsiya = new Point(x, y);
    }

    @Override
    public KontentTip poluchitTip() {
        return KontentTip.SMAILIK;
    }

    // Геттеры
    public TipSmailika poluchitTipSmailika() { return tip; }
    public Point poluchitPozitsiyu() { return pozitsiya; }
}