package kollektsii;

import model.Slaid.Slaid;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Реализация коллекции слайдов на двусвязном списке
 */
public class SlaidSpisok implements SlaidKolleksiya {

    // Внутренний класс узла
    private static class Uzel {
        Slaid dannie;
        Uzel pred;
        Uzel sled;

        Uzel(Slaid dannie) {
            this.dannie = dannie;
            this.pred = null;
            this.sled = null;
        }
    }

    private Uzel golova;
    private Uzel hvost;
    private int razmer;

    public SlaidSpisok() {
        this.golova = null;
        this.hvost = null;
        this.razmer = 0;
    }

    @Override
    public void dobavit(Slaid slaid) {
        dobavitVKontse(slaid);
    }

    @Override
    public void dobavit(int index, Slaid slaid) {
        proveritIndexDlyaDobavleniya(index);

        if (index == 0) {
            dobavitVNachalo(slaid);
        } else if (index == razmer) {
            dobavitVKontse(slaid);
        } else {
            dobavitVSeredinu(index, slaid);
        }
    }

    private void dobavitVNachalo(Slaid slaid) {
        Uzel noviyUzel = new Uzel(slaid);

        if (pusto()) {
            golova = hvost = noviyUzel;
        } else {
            noviyUzel.sled = golova;
            golova.pred = noviyUzel;
            golova = noviyUzel;
        }
        razmer++;
    }

    private void dobavitVKontse(Slaid slaid) {
        Uzel noviyUzel = new Uzel(slaid);

        if (pusto()) {
            golova = hvost = noviyUzel;
        } else {
            noviyUzel.pred = hvost;
            hvost.sled = noviyUzel;
            hvost = noviyUzel;
        }
        razmer++;
    }

    private void dobavitVSeredinu(int index, Slaid slaid) {
        Uzel tekushiy = poluchitUzel(index);
        Uzel predidushiy = tekushiy.pred;

        Uzel noviyUzel = new Uzel(slaid);
        noviyUzel.pred = predidushiy;
        noviyUzel.sled = tekushiy;

        predidushiy.sled = noviyUzel;
        tekushiy.pred = noviyUzel;

        razmer++;
    }

    @Override
    public void udalit(Slaid slaid) {
        if (pusto()) return;

        Uzel tekushiy = golova;
        while (tekushiy != null) {
            if (tekushiy.dannie.equals(slaid)) {
                udalitUzel(tekushiy);
                return;
            }
            tekushiy = tekushiy.sled;
        }
    }

    @Override
    public void udalit(int index) {
        proveritIndex(index);
        Uzel uzel = poluchitUzel(index);
        udalitUzel(uzel);
    }

    private void udalitUzel(Uzel uzel) {
        if (uzel.pred == null) {
            golova = uzel.sled;
        } else {
            uzel.pred.sled = uzel.sled;
        }

        if (uzel.sled == null) {
            hvost = uzel.pred;
        } else {
            uzel.sled.pred = uzel.pred;
        }

        uzel.dannie = null;
        razmer--;
    }

    @Override
    public Slaid poluchit(int index) {
        proveritIndex(index);
        return poluchitUzel(index).dannie;
    }

    private Uzel poluchitUzel(int index) {
        if (index < razmer / 2) {
            Uzel tekushiy = golova;
            for (int i = 0; i < index; i++) {
                tekushiy = tekushiy.sled;
            }
            return tekushiy;
        } else {
            Uzel tekushiy = hvost;
            for (int i = razmer - 1; i > index; i--) {
                tekushiy = tekushiy.pred;
            }
            return tekushiy;
        }
    }

    @Override
    public int razmer() {
        return razmer;
    }

    @Override
    public boolean pusto() {
        return razmer == 0;
    }

    @Override
    public void ochistit() {
        Uzel tekushiy = golova;
        while (tekushiy != null) {
            Uzel sleduyushiy = tekushiy.sled;
            tekushiy.dannie = null;
            tekushiy.pred = null;
            tekushiy.sled = null;
            tekushiy = sleduyushiy;
        }
        golova = hvost = null;
        razmer = 0;
    }

    @Override
    public void pomenyatMesta(int index1, int index2) {
        if (index1 == index2) return;

        proveritIndex(index1);
        proveritIndex(index2);

        Slaid temp = poluchit(index1);
        poluchitUzel(index1).dannie = poluchit(index2);
        poluchitUzel(index2).dannie = temp;
    }

    @Override
    public void peremestit(int otIndex, int kIndex) {
        if (otIndex == kIndex) return;

        proveritIndex(otIndex);
        proveritIndexDlyaDobavleniya(kIndex);

        Slaid slaid = poluchit(otIndex);
        udalit(otIndex);

        int noviyIndex = kIndex > otIndex ? kIndex - 1 : kIndex;
        dobavit(noviyIndex, slaid);
    }

    @Override
    public void obratniyPoryadok() {
        if (razmer <= 1) return;

        Uzel tekushiy = golova;
        Uzel temp = null;

        while (tekushiy != null) {
            temp = tekushiy.pred;
            tekushiy.pred = tekushiy.sled;
            tekushiy.sled = temp;
            tekushiy = tekushiy.pred;
        }

        temp = golova;
        golova = hvost;
        hvost = temp;
    }

    @Override
    public int naytiIndex(Slaid slaid) {
        Uzel tekushiy = golova;
        int index = 0;

        while (tekushiy != null) {
            if (tekushiy.dannie.equals(slaid)) {
                return index;
            }
            tekushiy = tekushiy.sled;
            index++;
        }

        return -1;
    }

    @Override
    public boolean soderzhit(Slaid slaid) {
        return naytiIndex(slaid) != -1;
    }

    @Override
    public Iterator<Slaid> iterator() {
        return new SlaidIterator(golova, false);
    }

    @Override
    public Iterator<Slaid> iteratorSkonca() {
        return new SlaidIterator(hvost, true);
    }

    @Override
    public Iterator<Slaid> iteratorSOpredelennogoMesta(int startIndex) {
        proveritIndex(startIndex);
        Uzel startUzel = poluchitUzel(startIndex);
        return new SlaidIterator(startUzel, false);
    }

    // Внутренний класс итератора
    private static class SlaidIterator implements Iterator<Slaid> {
        private Uzel tekushiy;
        private boolean sKontsa;

        SlaidIterator(Uzel startUzel, boolean sKontsa) {
            this.tekushiy = startUzel;
            this.sKontsa = sKontsa;
        }

        @Override
        public boolean hasNext() {
            return tekushiy != null;
        }

        @Override
        public Slaid next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Slaid dannie = tekushiy.dannie;

            if (sKontsa) {
                tekushiy = tekushiy.pred;
            } else {
                tekushiy = tekushiy.sled;
            }

            return dannie;
        }
    }

    private void proveritIndex(int index) {
        if (index < 0 || index >= razmer) {
            throw new IndexOutOfBoundsException(
                    "Индекс: " + index + ", Размер: " + razmer
            );
        }
    }

    private void proveritIndexDlyaDobavleniya(int index) {
        if (index < 0 || index > razmer) {
            throw new IndexOutOfBoundsException(
                    "Индекс: " + index + ", Размер: " + razmer
            );
        }
    }

    public Slaid poluchitPerviy() {
        if (pusto()) {
            throw new NoSuchElementException("Коллекция пуста");
        }
        return golova.dannie;
    }

    public Slaid poluchitPosledniy() {
        if (pusto()) {
            throw new NoSuchElementException("Коллекция пуста");
        }
        return hvost.dannie;
    }

    public Slaid[] vMassiv() {
        if (pusto()) {
            return new Slaid[0];
        }

        Slaid[] massiv = new Slaid[razmer];
        Uzel tekushiy = golova;
        for (int i = 0; i < razmer; i++) {
            massiv[i] = tekushiy.dannie;
            tekushiy = tekushiy.sled;
        }
        return massiv;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SlaidSpisok [");

        Uzel tekushiy = golova;
        while (tekushiy != null) {
            sb.append(tekushiy.dannie.poluchitId());
            if (tekushiy.sled != null) {
                sb.append(" -> ");
            }
            tekushiy = tekushiy.sled;
        }

        sb.append("] (размер: ").append(razmer).append(")");
        return sb.toString();
    }
}