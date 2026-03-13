package ui;

import builders.ViewState;
import fabriki.IzobrazhenieSlaidFabrika;
import fabriki.SlaidFabrika;
import kollektsii.SlaidKolleksiya;
import kollektsii.SlaidSpisok;
import model.Slaid.IzobrazhenieSlaid;
import model.Slaid.Slaid;
import model.animatsiya.Animatsiya;
import model.kontent.Kontent;
import persistence.ProektSaver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GlavnoeOkno extends JFrame {

    private SlaidKolleksiya kollektsiya;
    private ViewState sostoyanie;
    private SlaidFabrika fabrika;
    private AnimationService animationService;
    private Map<String, Object> nastroykiProekta;

    // Компоненты
    private JPanel panelSlaida;
    private SlaidPanel slaidPanel;
    private JTextArea textAreaZametka;
    private JButton btnPred, btnSled, btnPerviy, btnPosledniy;
    private JLabel labelProgress;
    private JComboBox<String> comboAnimatsii;
    private JSlider sliderSkorost;
    private JCheckBox checkZametki, checkAnimatsiya;

    public GlavnoeOkno() {
        kollektsiya = new SlaidSpisok();
        fabrika = new IzobrazhenieSlaidFabrika();
        animationService = new AnimationService();
        nastroykiProekta = new HashMap<>();
        nastroykiProekta.put("nazvanie", "Новый проект");
        nastroykiProekta.put("skorost_animatsii", 5);
        nastroykiProekta.put("pokaz_zametok", true);
        nastroykiProekta.put("ispolzovat_animatsiyu", true);

        sostoyanie = ViewState.sozdatNachalnoeSostoyanie();

        initComponents();
        obnovitInterfeis();
    }

    // Внутренний класс для отображения слайда с контентом
    class SlaidPanel extends JPanel {
        private IzobrazhenieSlaid slaid;
        private BufferedImage otobrazhenie;

        public void ustanovitSlaid(IzobrazhenieSlaid slaid) {
            this.slaid = slaid;
            if (slaid != null) {
                slaid.otobrazhit();
                this.otobrazhenie = slaid.poluchitBuferIzobrazheniya();
            } else {
                this.otobrazhenie = null;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (otobrazhenie != null) {
                // Масштабируем изображение под размер панели
                double panelWidth = getWidth();
                double panelHeight = getHeight();
                double imageWidth = otobrazhenie.getWidth();
                double imageHeight = otobrazhenie.getHeight();

                double scale = Math.min(panelWidth / imageWidth, panelHeight / imageHeight);
                int scaledWidth = (int)(imageWidth * scale);
                int scaledHeight = (int)(imageHeight * scale);
                int x = (int)((panelWidth - scaledWidth) / 2);
                int y = (int)((panelHeight - scaledHeight) / 2);

                g.drawImage(otobrazhenie, x, y, scaledWidth, scaledHeight, this);

                // Рисуем контент поверх
                if (slaid != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.translate(x, y);
                    g2d.scale(scale, scale);

                    for (Kontent kontent : slaid.poluchitSpisokKontenta()) {
                        kontent.risovat(g2d,
                                new Rectangle(0, 0, otobrazhenie.getWidth(), otobrazhenie.getHeight()));
                    }
                    g2d.dispose();
                }
            } else {
                // Отображаем заглушку
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.DARK_GRAY);
                g.drawString("Нет изображения", getWidth()/2 - 40, getHeight()/2);
            }
        }
    }

    private void initComponents() {
        setTitle("AlbomBpechatleniu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Главный контейнер
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Панель слайда
        panelSlaida = new JPanel(new BorderLayout());
        slaidPanel = new SlaidPanel();
        slaidPanel.setBackground(Color.WHITE);
        panelSlaida.add(slaidPanel, BorderLayout.CENTER);

        // Панель заметки
        JPanel panelZametka = new JPanel(new BorderLayout());
        panelZametka.setBorder(BorderFactory.createTitledBorder("Заметка к слайду"));
        textAreaZametka = new JTextArea(5, 20);
        textAreaZametka.setLineWrap(true);
        textAreaZametka.setWrapStyleWord(true);
        textAreaZametka.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { obnovitZametku(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { obnovitZametku(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { obnovitZametku(); }
            private void obnovitZametku() {
                if (!kollektsiya.pusto()) {
                    Slaid tekushiy = kollektsiya.poluchit(sostoyanie.getTekushiyIndex());
                    tekushiy.ustanovitZametku(textAreaZametka.getText());
                }
            }
        });
        panelZametka.add(new JScrollPane(textAreaZametka), BorderLayout.CENTER);

        // Панель управления - ИСПРАВЛЕНО
        JPanel panelUpravlenie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] knopki = {
                "Загрузить изображения", "Добавить текст", "Добавить смайлик",
                "Сохранить слайд", "Сохранить проект", "Открыть проект"
        };

        // Создаем все основные кнопки
        for (String text : knopki) {
            JButton btn = new JButton(text);
            btn.addActionListener(new KnopkaObrabotchik(text));
            panelUpravlenie.add(btn);
        }

        // Отдельно добавляем кнопку очистки
        JButton btnOchistit = new JButton("Очистить все слайды");
        btnOchistit.addActionListener(e -> ochistitVseSlaidi());
        panelUpravlenie.add(btnOchistit);

        // Панель навигации
        JPanel panelNavigatsiya = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPerviy = sozdatKnopku("<< Первый", e -> perekluchitSlaid(0));
        btnPred = sozdatKnopku("< Предыдущий", e -> predidushiySlaid());
        labelProgress = new JLabel("0 / 0");
        labelProgress.setFont(new Font("Arial", Font.BOLD, 14));
        btnSled = sozdatKnopku("Следующий >", e -> sleduyushiySlaid());
        btnPosledniy = sozdatKnopku("Последний >>", e -> perekluchitSlaid(kollektsiya.razmer() - 1));

        panelNavigatsiya.add(btnPerviy);
        panelNavigatsiya.add(btnPred);
        panelNavigatsiya.add(labelProgress);
        panelNavigatsiya.add(btnSled);
        panelNavigatsiya.add(btnPosledniy);

        // Панель настроек
        JPanel panelNastroiki = new JPanel(new GridLayout(8, 1, 5, 5)); // Увеличили до 8 строк
        panelNastroiki.setBorder(BorderFactory.createTitledBorder("Настройки показа"));

        checkZametki = new JCheckBox("Показывать заметки", true);
        checkZametki.addActionListener(e -> perekluchitZametki());

        checkAnimatsiya = new JCheckBox("Авто-анимация при переключении", true);
        checkAnimatsiya.addActionListener(e -> {
            nastroykiProekta.put("avto_animatsiya", checkAnimatsiya.isSelected());
        });

        comboAnimatsii = new JComboBox<>(new String[]{
                "Без анимации", "Плавное появление", "Слева", "Справа", "Приближение", "Вращение"
        });
        comboAnimatsii.addActionListener(e -> obnovitAnimatsiyu());

        sliderSkorost = new JSlider(1, 10, 5);
        sliderSkorost.setMajorTickSpacing(1);
        sliderSkorost.setPaintTicks(true);
        sliderSkorost.setPaintLabels(true);
        sliderSkorost.addChangeListener(e -> obnovitSkorostAnimatsii());

        JButton btnTestAnimatsii = new JButton("Тест анимации");
        btnTestAnimatsii.addActionListener(e -> testAnimatsii());

        JButton btnOstanovitAnimatsiyu = new JButton("Стоп анимации");
        btnOstanovitAnimatsiyu.addActionListener(e -> ostanovitAnimatsiyu());

        // Добавляем компоненты в правильном порядке
        panelNastroiki.add(new JLabel("Анимация:"));
        panelNastroiki.add(comboAnimatsii);
        panelNastroiki.add(new JLabel("Скорость:"));
        panelNastroiki.add(sliderSkorost);
        panelNastroiki.add(checkZametki);
        panelNastroiki.add(checkAnimatsiya);
        panelNastroiki.add(btnTestAnimatsii);
        panelNastroiki.add(btnOstanovitAnimatsiyu);

        // Сборка
        mainPanel.add(panelUpravlenie, BorderLayout.NORTH);
        mainPanel.add(panelSlaida, BorderLayout.CENTER);
        mainPanel.add(panelZametka, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(panelNavigatsiya, BorderLayout.SOUTH);
        add(panelNastroiki, BorderLayout.EAST);

        // Меню
        sozdatMenu();
    }
    private void testAnimatsii() {
        if (kollektsiya.pusto()) {
            JOptionPane.showMessageDialog(this, "Нет слайдов для тестирования анимации");
            return;
        }

        Slaid tekushiy = kollektsiya.poluchit(sostoyanie.getTekushiyIndex());
        Animatsiya anim = tekushiy.poluchitAnimatsiyu();

        // Обновляем анимацию из UI
        model.animatsiya.TipAnimatsii[] vseTipi = model.animatsiya.TipAnimatsii.values();
        int selectedIndex = comboAnimatsii.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < vseTipi.length) {
            anim.ustanovitTip(vseTipi[selectedIndex]);
        }
        anim.ustanovitProdolzhitelnost(sliderSkorost.getValue() * 200);

        // Запускаем анимацию
        animationService.vipolnitAnimatsiyu(tekushiy, slaidPanel, anim);

        JOptionPane.showMessageDialog(this,
                "Анимация запущена: " + anim.poluchitTip() +
                        "\nДлительность: " + anim.poluchitProdolzhitelnost() + " мс");
    }



    private void ochistitVseSlaidi() {
        if (kollektsiya.pusto()) {
            JOptionPane.showMessageDialog(this, "Нет слайдов для очистки");
            return;
        }

        int otvet = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить все слайды?",
                "Очистка слайдов",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (otvet == JOptionPane.YES_OPTION) {
            kollektsiya.ochistit();
            sostoyanie = ViewState.sozdatNachalnoeSostoyanie();
            obnovitSlaid();
            JOptionPane.showMessageDialog(this, "Все слайды удалены");
        }
    }
    class KnopkaObrabotchik implements ActionListener {
        private String text;

        KnopkaObrabotchik(String text) {
            this.text = text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (text) {
                case "Загрузить изображения": zagruzitIzobrazheniya(); break;
                case "Добавить текст": dobavitTekst(); break;
                case "Добавить смайлик": dobavitSmailik(); break;
                case "Сохранить слайд": sohranitSlaid(); break;
                case "Сохранить проект": sohranitProekt(); break;
                case "Открыть проект": otkritProekt(); break;
            }
        }
    }

    private JButton sozdatKnopku(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.addActionListener(listener);
        return btn;
    }

    private void sozdatMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFail = new JMenu("Файл");

        menuFail.add(sozdatMenuItem("Новый проект", e -> noviyProekt()));
        menuFail.add(sozdatMenuItem("Открыть проект", e -> otkritProekt()));
        menuFail.add(sozdatMenuItem("Сохранить проект", e -> sohranitProekt()));
        menuFail.add(sozdatMenuItem("Сохранить проект как", e -> sohranitProektKak()));
        menuFail.addSeparator();
        menuFail.add(sozdatMenuItem("Выход", e -> System.exit(0)));

        JMenu menuRedakt = new JMenu("Редактировать");
        menuRedakt.add(sozdatMenuItem("Изменить порядок слайдов", e -> izmenitPoryadok()));

        menuBar.add(menuFail);
        menuBar.add(menuRedakt);
        setJMenuBar(menuBar);
    }

    private JMenuItem sozdatMenuItem(String text, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        return item;
    }

    private void zagruzitIzobrazheniya() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() ||
                        f.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|bmp)$");
            }
            public String getDescription() {
                return "Изображения (*.jpg, *.png, *.gif, *.bmp)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                try {
                    Slaid slaid = fabrika.sozdatSlaid(file);
                    if (slaid instanceof IzobrazhenieSlaid) {
                        ((IzobrazhenieSlaid)slaid).ustanovitOriginalnoeIzobrazhenie(
                                javax.imageio.ImageIO.read(file));
                    }
                    kollektsiya.dobavit(slaid);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка загрузки: " + file.getName() + "\n" + e.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (kollektsiya.razmer() > 0) {
                sostoyanie = new ViewState.Builder()
                        .setTekushiyIndex(0)
                        .setVsegoSlaidi(kollektsiya.razmer())
                        .build();
                obnovitSlaid();
            }
        }
    }

    private void obnovitSlaid() {
        if (kollektsiya.pusto()) {
            slaidPanel.ustanovitSlaid(null);
            textAreaZametka.setText("");
            labelProgress.setText("0 / 0");
            return;
        }

        Slaid tekushiy = kollektsiya.poluchit(sostoyanie.getTekushiyIndex());

        if (tekushiy instanceof IzobrazhenieSlaid) {
            slaidPanel.ustanovitSlaid((IzobrazhenieSlaid) tekushiy);
            textAreaZametka.setText(tekushiy.poluchitZametku());

            // Обновляем настройки анимации
            Animatsiya anim = tekushiy.poluchitAnimatsiyu();
            model.animatsiya.TipAnimatsii[] vseTipi = model.animatsiya.TipAnimatsii.values();

            // Синхронизируем комбобокс с анимацией
            for (int i = 0; i < vseTipi.length; i++) {
                if (vseTipi[i] == anim.poluchitTip()) {
                    comboAnimatsii.setSelectedIndex(i);
                    break;
                }
            }
            sliderSkorost.setValue(anim.poluchitProdolzhitelnost() / 200);
        }

        labelProgress.setText(sostoyanie.getProgressText());
        obnovitNavigatsiyu();

        // Автоматически запускаем анимацию если включена
        if (checkAnimatsiya.isSelected()) {
            Slaid tekushiySlaid = kollektsiya.poluchit(sostoyanie.getTekushiyIndex());
            Animatsiya anim = tekushiySlaid.poluchitAnimatsiyu();
            if (anim.poluchitTip() != model.animatsiya.TipAnimatsii.NET) {
                // Небольшая задержка перед анимацией
                Timer timer = new Timer(100, e -> {
                    animationService.vipolnitAnimatsiyu(tekushiySlaid, slaidPanel, anim);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }



    private void predidushiySlaid() {
        if (sostoyanie.getTekushiyIndex() > 0) {
            sostoyanie = new ViewState.Builder()
                    .setTekushiyIndex(sostoyanie.getTekushiyIndex() - 1)
                    .setVsegoSlaidi(kollektsiya.razmer())
                    .build();
            obnovitSlaid();
        }
    }

    private void sleduyushiySlaid() {
        if (sostoyanie.getTekushiyIndex() < kollektsiya.razmer() - 1) {
            sostoyanie = new ViewState.Builder()
                    .setTekushiyIndex(sostoyanie.getTekushiyIndex() + 1)
                    .setVsegoSlaidi(kollektsiya.razmer())
                    .build();
            obnovitSlaid();
        }
    }

    private void perekluchitSlaid(int index) {
        if (index >= 0 && index < kollektsiya.razmer()) {
            sostoyanie = new ViewState.Builder()
                    .setTekushiyIndex(index)
                    .setVsegoSlaidi(kollektsiya.razmer())
                    .build();
            obnovitSlaid();
        }
    }

    private void obnovitNavigatsiyu() {
        boolean estSlaidi = !kollektsiya.pusto();
        btnPred.setEnabled(estSlaidi && !sostoyanie.isPerviySlaid());
        btnPerviy.setEnabled(estSlaidi && !sostoyanie.isPerviySlaid());
        btnSled.setEnabled(estSlaidi && !sostoyanie.isPosledniySlaid());
        btnPosledniy.setEnabled(estSlaidi && !sostoyanie.isPosledniySlaid());
    }

    private void dobavitTekst() {
        if (kollektsiya.pusto()) {
            JOptionPane.showMessageDialog(this, "Сначала загрузите слайд");
            return;
        }

        new RedaktorTeksta(this, kollektsiya.poluchit(sostoyanie.getTekushiyIndex())).setVisible(true);
        obnovitSlaid();
    }

    private void dobavitSmailik() {
        if (kollektsiya.pusto()) {
            JOptionPane.showMessageDialog(this, "Сначала загрузите слайд");
            return;
        }

        new RedaktorSmailika(this, kollektsiya.poluchit(sostoyanie.getTekushiyIndex())).setVisible(true);
        obnovitSlaid();
    }

    private void sohranitSlaid() {
        if (kollektsiya.pusto()) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("slaid.png"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
            }
            public String getDescription() {
                return "PNG изображения (*.png)";
            }
        });

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fail = fileChooser.getSelectedFile();
            if (!fail.getName().toLowerCase().endsWith(".png")) {
                fail = new File(fail.getAbsolutePath() + ".png");
            }

            try {
                Slaid tekushiy = kollektsiya.poluchit(sostoyanie.getTekushiyIndex());
                if (tekushiy instanceof IzobrazhenieSlaid) {
                    ((IzobrazhenieSlaid)tekushiy).otobrazhit();
                    BufferedImage image = ((IzobrazhenieSlaid)tekushiy).poluchitBuferIzobrazheniya();
                    ImageIO.write(image, "PNG", fail);
                    JOptionPane.showMessageDialog(this, "Слайд сохранен!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
            }
        }
    }

    private void sohranitProekt() {
        if (kollektsiya.pusto()) {
            JOptionPane.showMessageDialog(this, "Нет слайдов для сохранения");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Выберите папку для проекта");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File papka = fileChooser.getSelectedFile();
            try {
                // Обновляем настройки
                nastroykiProekta.put("skorost_animatsii", sliderSkorost.getValue());
                nastroykiProekta.put("ispolzovat_animatsiyu", checkAnimatsiya.isSelected());
                nastroykiProekta.put("pokaz_zametok", checkZametki.isSelected());

                ProektSaver.sohranitProekt(kollektsiya, papka.getAbsolutePath(), nastroykiProekta);
                JOptionPane.showMessageDialog(this, "Проект сохранен в: " + papka.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + e.getMessage());
            }
        }
    }

    private void sohranitProektKak() {
        String nazvanie = JOptionPane.showInputDialog(this, "Введите название проекта:");
        if (nazvanie != null && !nazvanie.trim().isEmpty()) {
            nastroykiProekta.put("nazvanie", nazvanie.trim());
            sohranitProekt();
        }
    }

    private void otkritProekt() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Выберите папку проекта");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                kollektsiya = ProektSaver.zagruzitProekt(fileChooser.getSelectedFile().getAbsolutePath());
                sostoyanie = new ViewState.Builder()
                        .setTekushiyIndex(0)
                        .setVsegoSlaidi(kollektsiya.razmer())
                        .build();
                obnovitSlaid();
                JOptionPane.showMessageDialog(this, "Проект загружен!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки: " + e.getMessage());
            }
        }
    }

    private void noviyProekt() {
        if (!kollektsiya.pusto()) {
            int otvet = JOptionPane.showConfirmDialog(this,
                    "Сохранить текущий проект?", "Новый проект",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (otvet == JOptionPane.YES_OPTION) {
                sohranitProekt();
            } else if (otvet == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        kollektsiya = new SlaidSpisok();
        sostoyanie = ViewState.sozdatNachalnoeSostoyanie();
        obnovitSlaid();
    }

    private void izmenitPoryadok() {
        if (kollektsiya.razmer() < 2) {
            JOptionPane.showMessageDialog(this, "Нужно хотя бы 2 слайда");
            return;
        }
        new RedaktorPoryadka(this, kollektsiya).setVisible(true);
        obnovitSlaid();
    }

    private void perekluchitZametki() {
        textAreaZametka.setVisible(checkZametki.isSelected());
    }

    private void obnovitAnimatsiyu() {
        if (!kollektsiya.pusto()) {
            Slaid tekushiy = kollektsiya.poluchit(sostoyanie.getTekushiyIndex());
            Animatsiya anim = tekushiy.poluchitAnimatsiyu();

            model.animatsiya.TipAnimatsii[] vseTipi = model.animatsiya.TipAnimatsii.values();
            int selectedIndex = comboAnimatsii.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < vseTipi.length) {
                anim.ustanovitTip(vseTipi[selectedIndex]);
            }

            // Сохраняем настройку в проекте
            nastroykiProekta.put("tip_animatsii", comboAnimatsii.getSelectedItem().toString());
        }
    }

    private void ostanovitAnimatsiyu() {
        animationService.ostanovitAnimatsiyu();
        JOptionPane.showMessageDialog(this, "Анимация остановлена");
    }

    private void obnovitSkorostAnimatsii() {
        if (!kollektsiya.pusto()) {
            Slaid tekushiy = kollektsiya.poluchit(sostoyanie.getTekushiyIndex());
            Animatsiya anim = tekushiy.poluchitAnimatsiyu();
            anim.ustanovitProdolzhitelnost(sliderSkorost.getValue() * 200);
        }
    }

    private void obnovitInterfeis() {
        obnovitSlaid();
        obnovitNavigatsiyu();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            GlavnoeOkno okno = new GlavnoeOkno();
            okno.setVisible(true);
        });
    }
}