package model.animatsiya;

public class Animatsiya {
    private TipAnimatsii tip;
    private int prodolzhitelnost;

    // Конструкторы
    public Animatsiya() {
        this.tip = TipAnimatsii.PLANKO;
        this.prodolzhitelnost = 1000;
    }

    public Animatsiya(TipAnimatsii tip, int prodolzhitelnost) {
        this.tip = tip;
        this.prodolzhitelnost = prodolzhitelnost;
    }

    // Геттеры и сеттеры
    public TipAnimatsii poluchitTip() {
        return tip;
    }

    public void ustanovitTip(TipAnimatsii tip) {
        this.tip = tip;
    }

    public int poluchitProdolzhitelnost() {
        return prodolzhitelnost;
    }

    public void ustanovitProdolzhitelnost(int prodolzhitelnost) {
        this.prodolzhitelnost = Math.max(100, Math.min(10000, prodolzhitelnost));
    }
}