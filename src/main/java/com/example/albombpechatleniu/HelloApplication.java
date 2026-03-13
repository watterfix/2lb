package com.example.albombpechatleniu;

import ui.GlavnoeOkno;
import javax.swing.*;

public class HelloApplication {
    public static void main(String[] args) {
        // Устанавливаем Look and Feel для лучшего вида
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Запускаем главное окно
        SwingUtilities.invokeLater(() -> {
            GlavnoeOkno okno = new GlavnoeOkno();
            okno.setVisible(true);
        });
    }
}