package ui;

import model.Slaid.Slaid;
import model.animatsiya.Animatsiya;
import model.animatsiya.TipAnimatsii;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnimationService {

    private ScheduledExecutorService executor;
    private boolean animatsiyaVProtsesse = false;

    public AnimationService() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void vipolnitAnimatsiyu(Slaid slaid, Component komponent, Animatsiya animatsiya) {
        if (animatsiya == null || komponent == null || komponent.getParent() == null) {
            return;
        }

        if (animatsiyaVProtsesse) {
            ostanovitAnimatsiyu();
        }

        animatsiyaVProtsesse = true;
        TipAnimatsii tip = animatsiya.poluchitTip();
        int prodolzhitelnost = animatsiya.poluchitProdolzhitelnost();

        if (tip == TipAnimatsii.NET) {
            komponent.setVisible(true);
            animatsiyaVProtsesse = false;
            return;
        }

        // Сохраняем оригинальное положение
        Point originalLocation = komponent.getLocation();

        switch (tip) {
            case PLANKO:
                vipolnitPlankoAnimatsiyu(komponent, prodolzhitelnost);
                break;
            case POYAVLENIE_SLEVA:
                vipolnitPoyavlenieSleva(komponent, prodolzhitelnost, originalLocation);
                break;
            case POYAVLENIE_SPRAVA:
                vipolnitPoyavlenieSprava(komponent, prodolzhitelnost, originalLocation);
                break;
            case PRIBLLIZHENIE:
                vipolnitPriblizhenie(komponent, prodolzhitelnost);
                break;
            case VRAЩENIE:
                vipolnitVrashchenie(komponent, prodolzhitelnost);
                break;
        }
    }

    private void vipolnitPlankoAnimatsiyu(Component komponent, int prodolzhitelnost) {
        komponent.setVisible(false);

        int shagov = 20;
        int zaderzhka = prodolzhitelnost / shagov;

        executor.scheduleAtFixedRate(new Runnable() {
            int shag = 0;

            @Override
            public void run() {
                if (shag >= shagov) {
                    komponent.setVisible(true);
                    animatsiyaVProtsesse = false;
                    executor.shutdown();
                    executor = Executors.newSingleThreadScheduledExecutor();
                    throw new RuntimeException("Cancel");
                }

                float prozrachnost = (float) shag / shagov;
                if (komponent instanceof JComponent) {
                    ((JComponent) komponent).setOpaque(false);
                }
                komponent.setVisible(true);
                komponent.repaint();
                shag++;
            }
        }, 0, zaderzhka, TimeUnit.MILLISECONDS);
    }

    private void vipolnitPoyavlenieSleva(Component komponent, int prodolzhitelnost, Point originalLocation) {
        Container parent = komponent.getParent();
        int startX = -komponent.getWidth();
        int endX = originalLocation.x;

        komponent.setLocation(startX, originalLocation.y);
        komponent.setVisible(true);

        animirovatDvizhenie(komponent, startX, originalLocation.y, endX, originalLocation.y, prodolzhitelnost);
    }

    private void vipolnitPoyavlenieSprava(Component komponent, int prodolzhitelnost, Point originalLocation) {
        Container parent = komponent.getParent();
        int startX = parent.getWidth();
        int endX = originalLocation.x;

        komponent.setLocation(startX, originalLocation.y);
        komponent.setVisible(true);

        animirovatDvizhenie(komponent, startX, originalLocation.y, endX, originalLocation.y, prodolzhitelnost);
    }

    private void vipolnitPriblizhenie(Component komponent, int prodolzhitelnost) {
        komponent.setVisible(true);
        // Простая имитация приближения через изменение размера
        final Dimension originalSize = komponent.getSize();
        komponent.setSize(1, 1);

        int shagov = 30;
        int zaderzhka = prodolzhitelnost / shagov;

        executor.scheduleAtFixedRate(new Runnable() {
            int shag = 0;

            @Override
            public void run() {
                if (shag >= shagov) {
                    komponent.setSize(originalSize);
                    animatsiyaVProtsesse = false;
                    executor.shutdown();
                    executor = Executors.newSingleThreadScheduledExecutor();
                    throw new RuntimeException("Cancel");
                }

                float progress = (float) shag / shagov;
                int width = (int) (1 + (originalSize.width - 1) * progress);
                int height = (int) (1 + (originalSize.height - 1) * progress);
                komponent.setSize(width, height);
                komponent.repaint();
                shag++;
            }
        }, 0, zaderzhka, TimeUnit.MILLISECONDS);
    }

    private void vipolnitVrashchenie(Component komponent, int prodolzhitelnost) {
        komponent.setVisible(true);
        // Простая имитация вращения через изменение прозрачности
        int shagov = 36; // 10 градусов на шаг
        int zaderzhka = prodolzhitelnost / shagov;

        executor.scheduleAtFixedRate(new Runnable() {
            int shag = 0;

            @Override
            public void run() {
                if (shag >= shagov) {
                    animatsiyaVProtsesse = false;
                    executor.shutdown();
                    executor = Executors.newSingleThreadScheduledExecutor();
                    throw new RuntimeException("Cancel");
                }

                // Визуальный эффект вращения через перерисовку
                komponent.repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                shag++;
            }
        }, 0, zaderzhka, TimeUnit.MILLISECONDS);
    }

    private void animirovatDvizhenie(Component komponent, int startX, int startY, int endX, int endY, int prodolzhitelnost) {
        int shagov = 30;
        int zaderzhka = prodolzhitelnost / shagov;

        executor.scheduleAtFixedRate(new Runnable() {
            int shag = 0;

            @Override
            public void run() {
                if (shag > shagov) {
                    animatsiyaVProtsesse = false;
                    executor.shutdown();
                    executor = Executors.newSingleThreadScheduledExecutor();
                    throw new RuntimeException("Cancel");
                }

                float progress = (float) shag / shagov;
                int tekushiyX = (int) (startX + (endX - startX) * progress);
                int tekushiyY = (int) (startY + (endY - startY) * progress);

                komponent.setLocation(tekushiyX, tekushiyY);
                komponent.getParent().repaint();

                shag++;
            }
        }, 0, zaderzhka, TimeUnit.MILLISECONDS);
    }

    public void ostanovitAnimatsiyu() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        animatsiyaVProtsesse = false;
    }

    public boolean isAnimatsiyaVProtsesse() {
        return animatsiyaVProtsesse;
    }
}