package ui;

import model.Slaid.Slaid;
import kollektsii.SlaidKolleksiya;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RedaktorPoryadka extends JDialog {

    private SlaidKolleksiya kollektsiya;
    private DefaultListModel<String> listModel;
    private JList<String> listSlaidi;

    public RedaktorPoryadka(JFrame parent, SlaidKolleksiya kollektsiya) {
        super(parent, "Редактор порядка слайдов", true);
        this.kollektsiya = kollektsiya;

        initComponents();
        zagruzitSlaidi();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Панель списка
        listModel = new DefaultListModel<>();
        listSlaidi = new JList<>(listModel);
        listSlaidi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(listSlaidi);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // Панель кнопок
        JPanel panelKnopki = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnVverh = new JButton("↑ Вверх");
        btnVverh.addActionListener(e -> peremestitVverh());

        JButton btnVniz = new JButton("↓ Вниз");
        btnVniz.addActionListener(e -> peremestitVniz());

        JButton btnPomenyat = new JButton("↔ Поменять");
        btnPomenyat.addActionListener(e -> pomenyatMesta());

        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(e -> dispose());

        JButton btnOtm = new JButton("Отмена");
        btnOtm.addActionListener(e -> dispose());

        panelKnopki.add(btnVverh);
        panelKnopki.add(btnVniz);
        panelKnopki.add(btnPomenyat);
        panelKnopki.add(Box.createHorizontalStrut(20));
        panelKnopki.add(btnOK);
        panelKnopki.add(btnOtm);

        mainPanel.add(new JLabel("Порядок слайдов:"), BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(panelKnopki, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void zagruzitSlaidi() {
        listModel.clear();
        for (Slaid slaid : kollektsiya) {
            listModel.addElement(slaid.poluchitId());
        }
    }

    private void peremestitVverh() {
        int index = listSlaidi.getSelectedIndex();
        if (index > 0) {
            kollektsiya.peremestit(index, index - 1);
            zagruzitSlaidi();
            listSlaidi.setSelectedIndex(index - 1);
        }
    }

    private void peremestitVniz() {
        int index = listSlaidi.getSelectedIndex();
        if (index >= 0 && index < kollektsiya.razmer() - 1) {
            kollektsiya.peremestit(index, index + 1);
            zagruzitSlaidi();
            listSlaidi.setSelectedIndex(index + 1);
        }
    }

    private void pomenyatMesta() {
        int index1 = listSlaidi.getSelectedIndex();
        if (index1 < 0) return;

        String input = JOptionPane.showInputDialog(this,
                "Введите номер слайда для обмена (1-" + kollektsiya.razmer() + "):");

        if (input != null) {
            try {
                int index2 = Integer.parseInt(input) - 1;
                if (index2 >= 0 && index2 < kollektsiya.razmer()) {
                    kollektsiya.pomenyatMesta(index1, index2);
                    zagruzitSlaidi();
                    listSlaidi.setSelectedIndex(index2);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Неверный номер");
            }
        }
    }
}