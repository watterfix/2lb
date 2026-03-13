package builders;

import model.Slaid.Slaid;
import kollektsii.SlaidKolleksiya;

/**
 * Расширенный строитель для состояний просмотра
 * Может создавать состояния на основе коллекции слайдов
 */
public class ViewStateBuilder {

    /**
     * Создать состояние из коллекции слайдов
     */
    public static ViewState izKolleksii(SlaidKolleksiya kollektsiya) {
        if (kollektsiya == null || kollektsiya.pusto()) {
            return ViewState.sozdatNachalnoeSostoyanie();
        }

        return new ViewState.Builder()
                .setTekushiyIndex(0)
                .setVsegoSlaidi(kollektsiya.razmer())
                .setPokazZametok(true)
                .build();
    }

    /**
     * Создать состояние для конкретного слайда
     */
    public static ViewState dlyaKonkretnogoSlaida(Slaid slaid, SlaidKolleksiya kollektsiya) {
        if (slaid == null || kollektsiya == null) {
            return ViewState.sozdatNachalnoeSostoyanie();
        }

        int index = kollektsiya.naytiIndex(slaid);
        if (index == -1) {
            index = 0;
        }

        return new ViewState.Builder()
                .setTekushiyIndex(index)
                .setVsegoSlaidi(kollektsiya.razmer())
                .build();
    }

    /**
     * Создать состояние для показа с автоматическим переходом
     */
    public static ViewState dlyaAvtopokaza(SlaidKolleksiya kollektsiya, int intervalSeconds) {
        ViewState.Builder builder = new ViewState.Builder()
                .setTekushiyIndex(0)
                .setPokazVProgresse(true)
                .setAvtoperehod(true)
                .setIntervalAvtoperehoda(intervalSeconds);

        if (kollektsiya != null) {
            builder.setVsegoSlaidi(kollektsiya.razmer());
        }

        return builder.build();
    }

    /**
     * Обновить существующее состояние
     */
    public static ViewState obnovit(ViewState staroeSostoyanie,
                                    SlaidKolleksiya novayaKolleksiya) {
        if (staroeSostoyanie == null) {
            return izKolleksii(novayaKolleksiya);
        }

        int noviyRazmer = novayaKolleksiya != null ? novayaKolleksiya.razmer() : 0;
        int tekushiyIndex = Math.min(staroeSostoyanie.getTekushiyIndex(),
                Math.max(0, noviyRazmer - 1));

        return new ViewState.Builder()
                .setTekushiyIndex(tekushiyIndex)
                .setVsegoSlaidi(noviyRazmer)
                .setPokazZametok(staroeSostoyanie.isPokazZametok())
                .setSkorostAnimatsii(staroeSostoyanie.getSkorostAnimatsii())
                .setAvtoperehod(staroeSostoyanie.isAvtoperehod())
                .setIntervalAvtoperehoda(staroeSostoyanie.getIntervalAvtoperehoda())
                .build();
    }
}