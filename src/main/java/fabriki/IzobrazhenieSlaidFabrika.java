package fabriki;

import fabriki.SlaidFabrika;
import model.Slaid.Slaid;
import model.Slaid.IzobrazhenieSlaid;
import model.kontent.Kontent;
import model.kontent.TekstKontent;
import model.kontent.SmailikKontent;
import model.kontent.UgolSlaida;
import model.animatsiya.Animatsiya;
import model.animatsiya.TipAnimatsii;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

public class IzobrazhenieSlaidFabrika implements SlaidFabrika {

    @Override
    public Slaid sozdatSlaid(File failIzobrazheniya) {
        try {
            Image izobrazhenie = ImageIO.read(failIzobrazheniya);
            IzobrazhenieSlaid slaid = new IzobrazhenieSlaid();
            slaid.ustanovitIzobrazhenie(izobrazhenie);

            String imyaFaila = failIzobrazheniya.getName();
            slaid.ustanovitId(imyaFaila.replace(".", "_"));

            return slaid;

        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения: " + e.getMessage());
            return sozdatPustoySlaid();
        }
    }

    private Slaid sozdatPustoySlaid() {
        IzobrazhenieSlaid slaid = new IzobrazhenieSlaid();
        slaid.ustanovitZametku("Не удалось загрузить изображение");
        return slaid;
    }

    @Override
    public Kontent sozdatTekstKontent(String tekst, UgolSlaida ugol, double masshtab) {
        return new TekstKontent(tekst, ugol, masshtab);
    }

    @Override
    public Kontent sozdatSmailikKontent(SmailikKontent.TipSmailika tipSmailika, int x, int y) {
        return new SmailikKontent(tipSmailika, new Point(x, y));
    }

    @Override
    public Animatsiya sozdatAnimatsiyu(TipAnimatsii tip, int prodolzhitelnost) {
        // Способ 1: Через конструктор (если он есть)
        // return new Animatsiya(tip, prodolzhitelnost);

        // Способ 2: Через создание и сеттеры (если конструктора нет)
        Animatsiya animatsiya = new Animatsiya();
        animatsiya.ustanovitTip(tip);
        animatsiya.ustanovitProdolzhitelnost(prodolzhitelnost);
        return animatsiya;
    }

    @Override
    public String sozdatZametku(String tekst) {
        return (tekst == null || tekst.trim().isEmpty())
                ? "Пустая заметка"
                : tekst.trim();
    }
}