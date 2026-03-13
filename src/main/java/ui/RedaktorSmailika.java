package ui;

import model.Slaid.Slaid;
import model.kontent.SmailikKontent;

import javax.swing.*;
import java.awt.*;

public class RedaktorSmailika extends JDialog {
    private Slaid slaid;
    private JComboBox<SmailikKontent.TipSmailika> comboTip;
    private JSpinner spinnerX, spinnerY;

    public RedaktorSmailika(JFrame parent, Slaid slaid) {
        super(parent, "Добавить смайлик", true);
        this.slaid = slaid;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Тип смайлика:"));
        comboTip = new JComboBox<>(SmailikKontent.TipSmailika.values());
        panel.add(comboTip);

        panel.add(new JLabel("Позиция X:"));
        spinnerX = new JSpinner(new SpinnerNumberModel(100, 0, 1000, 10));
        panel.add(spinnerX);

        panel.add(new JLabel("Позиция Y:"));
        spinnerY = new JSpinner(new SpinnerNumberModel(100, 0, 1000, 10));
        panel.add(spinnerY);

        JButton btnOk = new JButton("Добавить");
        btnOk.addActionListener(e -> dobavitSmailik());
        JButton btnCancel = new JButton("Отмена");
        btnCancel.addActionListener(e -> dispose());

        JPanel panelKnopki = new JPanel(new FlowLayout());
        panelKnopki.add(btnOk);
        panelKnopki.add(btnCancel);

        add(panel, BorderLayout.CENTER);
        add(panelKnopki, BorderLayout.SOUTH);

        setSize(400, 200);
    }

    private void dobavitSmailik() {
        SmailikKontent.TipSmailika tip = (SmailikKontent.TipSmailika) comboTip.getSelectedItem();
        int x = (Integer) spinnerX.getValue();
        int y = (Integer) spinnerY.getValue();

        SmailikKontent smailik = new SmailikKontent(tip, new Point(x, y));
        slaid.dobavitKontent(smailik);

        dispose();
    }
}