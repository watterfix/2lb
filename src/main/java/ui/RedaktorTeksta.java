package ui;

import model.Slaid.Slaid;
import model.kontent.TekstKontent;
import model.kontent.UgolSlaida;

import javax.swing.*;
import java.awt.*;

public class RedaktorTeksta extends JDialog {
    private Slaid slaid;
    private JTextField textField;
    private JComboBox<UgolSlaida> comboUgol;
    private JSpinner spinnerMasshtab;

    public RedaktorTeksta(JFrame parent, Slaid slaid) {
        super(parent, "Добавить текст", true);
        this.slaid = slaid;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Текст:"));
        textField = new JTextField(20);
        panel.add(textField);

        panel.add(new JLabel("Угол:"));
        comboUgol = new JComboBox<>(UgolSlaida.values());
        panel.add(comboUgol);

        panel.add(new JLabel("Масштаб (0.5-3.0):"));
        spinnerMasshtab = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 3.0, 0.1));
        panel.add(spinnerMasshtab);

        JButton btnOk = new JButton("Добавить");
        btnOk.addActionListener(e -> dobavitTekst());
        JButton btnCancel = new JButton("Отмена");
        btnCancel.addActionListener(e -> dispose());

        JPanel panelKnopki = new JPanel(new FlowLayout());
        panelKnopki.add(btnOk);
        panelKnopki.add(btnCancel);

        add(panel, BorderLayout.CENTER);
        add(panelKnopki, BorderLayout.SOUTH);

        setSize(400, 200);
    }

    private void dobavitTekst() {
        String tekst = textField.getText().trim();
        if (tekst.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите текст");
            return;
        }

        UgolSlaida ugol = (UgolSlaida) comboUgol.getSelectedItem();
        double masshtab = (Double) spinnerMasshtab.getValue();

        TekstKontent tekstKontent = new TekstKontent(tekst, ugol, masshtab);
        slaid.dobavitKontent(tekstKontent);

        dispose();
    }
}