package util;

import java.awt.Color;

/**
 * Константы проекта
 */
public class Konstanty {
    // Пути по умолчанию
    public static final String PUT_PO_UMOLCHANIYU = "./slaidi";
    public static final String PUT_K_RESURSAM = "./resursi";
    public static final String PUT_K_SMAILIKAM = PUT_K_RESURSAM + "/smailiki";

    // Настройки отображения
    public static final int STANDARTNAYA_SHIRINA_SLAIDA = 800;
    public static final int STANDARTNAYA_VISOTA_SLAIDA = 600;
    public static final Color TSVET_FONA = Color.WHITE;

    // Настройки текста
    public static final double MIN_MASSHTAB_TEKSTA = 0.5;
    public static final double MAX_MASSHTAB_TEKSTA = 3.0;
    public static final double STANDARTNIY_MASSHTAB_TEKSTA = 1.0;

    // Настройки анимации
    public static final int MIN_PRODOLZHITELNOST_ANIMATSII = 100; // мс
    public static final int MAX_PRODOLZHITELNOST_ANIMATSII = 10000; // мс
    public static final int STANDARTNAYA_PRODOLZHITELNOST = 1000;

    // Размеры смайликов
    public static final int RAZMER_SMAILIKA = 50;
}