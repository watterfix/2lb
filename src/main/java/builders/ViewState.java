package builders;

import model.Slaid.Slaid;

/**
 * Состояние просмотра слайдов
 * Хранит текущую позицию, прогресс, настройки отображения
 */
public class ViewState {
    private int tekushiyIndex;      // Текущий слайд (0-based)
    private int vsegoSlaidi;        // Всего слайдов
    private boolean pokazVProgresse; // Показ в процессе
    private boolean pokazZametok;   // Показывать заметки
    private int skorostAnimatsii;   // Скорость анимации (1-10)
    private boolean avtoperehod;    // Автопереход
    private int intervalAvtoperehoda; // Интервал в секундах

    // Приватный конструктор - создается только через Builder
    private ViewState(int tekushiyIndex, int vsegoSlaidi,
                      boolean pokazVProgresse, boolean pokazZametok,
                      int skorostAnimatsii, boolean avtoperehod,
                      int intervalAvtoperehoda) {
        this.tekushiyIndex = tekushiyIndex;
        this.vsegoSlaidi = vsegoSlaidi;
        this.pokazVProgresse = pokazVProgresse;
        this.pokazZametok = pokazZametok;
        this.skorostAnimatsii = skorostAnimatsii;
        this.avtoperehod = avtoperehod;
        this.intervalAvtoperehoda = intervalAvtoperehoda;
    }

    // Геттеры
    public int getTekushiyIndex() { return tekushiyIndex; }
    public int getVsegoSlaidi() { return vsegoSlaidi; }
    public boolean isPokazVProgresse() { return pokazVProgresse; }
    public boolean isPokazZametok() { return pokazZametok; }
    public int getSkorostAnimatsii() { return skorostAnimatsii; }
    public boolean isAvtoperehod() { return avtoperehod; }
    public int getIntervalAvtoperehoda() { return intervalAvtoperehoda; }

    // Вычисляемые свойства
    public double getProgress() {
        return vsegoSlaidi > 0 ? (double)(tekushiyIndex + 1) / vsegoSlaidi : 0;
    }

    public String getProgressText() {
        return String.format("%d / %d", tekushiyIndex + 1, vsegoSlaidi);
    }

    public boolean isPerviySlaid() {
        return tekushiyIndex == 0;
    }

    public boolean isPosledniySlaid() {
        return tekushiyIndex == vsegoSlaidi - 1;
    }

    // Внутренний Builder класс
    public static class Builder {
        private int tekushiyIndex = 0;
        private int vsegoSlaidi = 0;
        private boolean pokazVProgresse = false;
        private boolean pokazZametok = false;
        private int skorostAnimatsii = 5;
        private boolean avtoperehod = false;
        private int intervalAvtoperehoda = 5;

        public Builder setTekushiyIndex(int index) {
            this.tekushiyIndex = Math.max(0, index);
            return this;
        }

        public Builder setVsegoSlaidi(int count) {
            this.vsegoSlaidi = Math.max(0, count);
            return this;
        }

        public Builder setPokazVProgresse(boolean inProgress) {
            this.pokazVProgresse = inProgress;
            return this;
        }

        public Builder setPokazZametok(boolean showNotes) {
            this.pokazZametok = showNotes;
            return this;
        }

        public Builder setSkorostAnimatsii(int speed) {
            this.skorostAnimatsii = Math.max(1, Math.min(10, speed));
            return this;
        }

        public Builder setAvtoperehod(boolean auto) {
            this.avtoperehod = auto;
            return this;
        }

        public Builder setIntervalAvtoperehoda(int seconds) {
            this.intervalAvtoperehoda = Math.max(1, Math.min(60, seconds));
            return this;
        }

        public ViewState build() {
            // Корректируем текущий индекс, если он выходит за границы
            if (vsegoSlaidi > 0) {
                tekushiyIndex = Math.min(tekushiyIndex, vsegoSlaidi - 1);
            } else {
                tekushiyIndex = 0;
            }

            return new ViewState(
                    tekushiyIndex, vsegoSlaidi, pokazVProgresse,
                    pokazZametok, skorostAnimatsii, avtoperehod,
                    intervalAvtoperehoda
            );
        }
    }

    // Статические методы для создания стандартных состояний
    public static ViewState sozdatNachalnoeSostoyanie() {
        return new Builder()
                .setTekushiyIndex(0)
                .setPokazZametok(true)
                .setSkorostAnimatsii(5)
                .build();
    }

    public static ViewState sozdatSostoyanieDlyaPokaza(int vsegoSlaidi) {
        return new Builder()
                .setTekushiyIndex(0)
                .setVsegoSlaidi(vsegoSlaidi)
                .setPokazVProgresse(true)
                .setAvtoperehod(false)
                .build();
    }
}