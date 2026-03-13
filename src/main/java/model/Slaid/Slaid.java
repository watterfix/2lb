package model.Slaid;

import model.kontent.Kontent;
import model.animatsiya.Animatsiya;
import java.util.List;
import java.awt.Image;

/**
 * Абстрактный класс слайда, основа для всех типов слайдов
 */
public abstract class Slaid {
    protected String id;
    protected Image osnovnoeIzobrazhenie;
    protected List<Kontent> spisokKontenta;
    protected String zametka;
    protected Animatsiya animatsiya;

    public abstract void otobrazhit();
    public abstract void dobavitKontent(Kontent kontent);
    public abstract void udalitKontent(Kontent kontent);

    // Геттеры и сеттеры
    public String poluchitId() {
        return id;
    }

    public void ustanovitId(String id) {
        this.id = id;
    }

    public Image poluchitIzobrazhenie() {
        return osnovnoeIzobrazhenie;
    }

    public void ustanovitIzobrazhenie(Image izobrazhenie) {
        this.osnovnoeIzobrazhenie = izobrazhenie;
    }

    public String poluchitZametku() {
        return zametka;
    }

    public void ustanovitZametku(String zametka) {
        this.zametka = zametka;
    }

    public Animatsiya poluchitAnimatsiyu() {
        return animatsiya;
    }

    public void ustanovitAnimatsiyu(Animatsiya animatsiya) {
        this.animatsiya = animatsiya;
    }
}